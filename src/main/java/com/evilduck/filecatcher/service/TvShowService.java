package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.model.Episode;
import com.evilduck.filecatcher.model.Job;
import com.evilduck.filecatcher.model.Season;
import com.evilduck.filecatcher.model.TvShow;
import com.evilduck.filecatcher.respository.TvShowRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TvShowService extends FileService {

    private final Pattern SEASON_FOLDER_PATTERN = Pattern.compile(".*([0-9]+).*");
    private final Pattern SEASON_FROM_EPISODE_PATTERN = Pattern.compile(".*s([0-9]+).*");
    private final Pattern EPISODE_NUMBER_PATTERN = Pattern.compile(".*e([0-9]+).*");

    private final ZipManager zipManager;
    private final TvShowRepository tvShowRepository;
    private final JobQueueService jobQueueService;

    protected TvShowService(final FileDefaults fileDefaults,
                            final ZipManager zipManager,
                            final TvShowRepository tvShowRepository,
                            JobQueueService jobQueueService) {
        super(fileDefaults, "zip");
        this.zipManager = zipManager;
        this.tvShowRepository = tvShowRepository;
        this.jobQueueService = jobQueueService;
    }

    @Override
    public String save(final Resource media, final String contentType) throws IOException {
        if (correctContentType(contentType)) {
            final File tempFolder = zipManager.unzipAlbum(media);
            final FileWriter metadataWriter = new FileWriter(getMetadataFileFor(tempFolder));
            final String mediaName = media.getFilename();
            metadataWriter.append(mediaName).close();
            if (mediaName == null) throw new IncorrectFileFormatException("Error accessing Filename");
            if (isValidTvShowFolder(tempFolder)) {
                return tempFolder.getName();
            } else {
                throw new IncorrectFileFormatException("Unable to find at least one video file in every season");
            }
        } else {
            throw new IncorrectFileFormatException("File is not a ZIP archive");
        }
    }

    @Override
    public void process(List<String> jobIds) {
        for (String id : jobIds) {
            final File tvShowFolder = zipManager.getJobDirectory(id);
            final Job job = new Job(id, () -> processTvShow(tvShowFolder));
            jobQueueService.addJob(job);
        }
    }

    private void processTvShow(File tempFolder) {
        final File metadata = getMetadataFileFor(tempFolder);
        try {
            final TvShow tvShow = parseTvShow(tempFolder, getShowNameFromMetadata(metadata));
            tvShowRepository.saveTvShow(tvShow);
            log.info("Saved TV Show [{}]", tvShow.name());
        } catch (IOException e) {
            log.error("There was a problem processing TV Show with Job ID [{}] {}", tempFolder.getName(), e.getMessage());
        }
    }

    private TvShow parseTvShow(final File tempTvShowFolder, final String tvShowName) {
        final File[] seasonFolders = safeListDirectory(tempTvShowFolder);
        final TvShow tvShow = new TvShow(tvShowName.replaceFirst("\\..+$", ""), seasonFolders.length + 1);

        for (final File seasonFolder : seasonFolders) {
            if (!seasonFolder.isDirectory()) {
                log.info("Skipping non-directory file [{}] in TV Show processing for [{}]", seasonFolder.getName(), tempTvShowFolder.getName());
                continue;
            }
            final File[] episodeFiles = safeListDirectory(seasonFolder);
            for (int e = 0; e < episodeFiles.length; e++) {
                final File episodeFile = episodeFiles[e];
                final int currentSeasonNumber = parseSeasonNumberFromEpisode(episodeFile.getName())
                        .orElse(parseSeasonNumberFromFolder(seasonFolder.getName())
                                .orElse(-1));
                final Season existingSeason = tvShow.getSeason(currentSeasonNumber);
                final Season season = existingSeason == null ? new Season(currentSeasonNumber, episodeFiles.length) : existingSeason;
                if (existingSeason == null) tvShow.addSeason(season, season.seasonNumber());
                final Episode episode = parseEpisode(tvShow.name(), season, episodeFile);
                season.addEpisode(e, episode);
            }
        }
        return tvShow;
    }

    private Episode parseEpisode(final String tvShowName,
                                 final Season season,
                                 final File episodeFile) {
        final String episodeNameRaw = episodeFile.getName();
        final int episodeNumber = parseEpisodeNumber(episodeNameRaw).orElse(-1);
        final Episode episode = new Episode(episodeNumber);
        final String fileExtension = parseExtension(episodeNameRaw);
        final String episodeName = cleanEpisodeName(tvShowName, season, episodeNumber, fileExtension);
        episode.setFile(episodeFile);
        episode.setExtension(fileExtension);
        episode.setName(episodeName);
        return episode;
    }

    private Optional<Integer> parseSeasonNumberFromFolder(final String folderName) {
        return parseIntegerUsingPattern(folderName, SEASON_FOLDER_PATTERN);
    }

    private Optional<Integer> parseSeasonNumberFromEpisode(final String episodeNameRaw) {
        return parseIntegerUsingPattern(episodeNameRaw, SEASON_FROM_EPISODE_PATTERN);
    }

    private Optional<Integer> parseEpisodeNumber(final String episodeNameRaw) {
        return parseIntegerUsingPattern(episodeNameRaw, EPISODE_NUMBER_PATTERN);
    }

    private static Optional<Integer> parseIntegerUsingPattern(final String fileSystemName,
                                                              final Pattern integerFindingPattern) {
        final Matcher seasonMatcher = integerFindingPattern.matcher(fileSystemName);
        if (seasonMatcher.matches()) {
            return Optional.ofNullable(seasonMatcher.group(1)).map(Integer::parseInt);
        }
        return Optional.empty();
    }


    private String cleanEpisodeName(final String tvShowName,
                                    final Season season,
                                    final int episodeNumber,
                                    final String fileExtension) {
        return String.format("%s_s%02de%02d.%s",
                tvShowName,
                season.seasonNumber(),
                episodeNumber,
                fileExtension
        );
    }

    private File[] safeListDirectory(final File directory) {
        final File[] list = directory.listFiles();
        if (list == null) throw new RuntimeException("Unable to list directory [" + directory.getPath() + ']');
        return list;
    }

    private boolean isValidTvShowFolder(final File folder) {
        final String[] seasons = folder.list();
        if (seasons == null) return false;
        for (String season : seasons) {
            final File seasonFolder = Path.of(folder.getPath(), season).toFile();
            if (!seasonFolder.isDirectory()) {
                log.info("Found file at root of TV Show [{}]", seasonFolder.getName());
                continue;
            }
            final String[] episodePaths = seasonFolder.list();
            if (episodePaths == null) return false;
            for (String episode : episodePaths) {
                final String contentType = URLConnection.guessContentTypeFromName(seasonFolder.toPath().resolve(episode).toString());
                if (contentType != null) {
                    if (contentType.startsWith("video/")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getShowNameFromMetadata(final File metadata) throws IOException {
        final List<String> lines = FileUtils.readLines(metadata, Charset.defaultCharset());
        if (lines.isEmpty()) throw new IllegalStateException("TV Show saved without name in metadata");
        else return lines.get(0);
    }

    private static File getMetadataFileFor(File tempFolder) {
        return tempFolder.toPath().resolve("metadata").toFile();
    }

}
