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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class TvShowService extends FileService {

    private final Pattern SEASON_FOLDER_PATTERN = Pattern.compile(".*([0-9]+).*");
    private final Pattern SEASON_FROM_EPISODE_PATTERN = Pattern.compile(".*[Ss]([0-9]+).*");
    private final Pattern EPISODE_NUMBER_PATTERN = Pattern.compile(".*[Ee]([0-9]+).*");

    private final JobDirectoryManager jobDirectoryManager;
    private final TvShowRepository tvShowRepository;
    private final JobQueueService jobQueueService;

    protected TvShowService(final FileDefaults fileDefaults,
                            final JobDirectoryManager jobDirectoryManager,
                            final TvShowRepository tvShowRepository,
                            JobQueueService jobQueueService) {
        super(fileDefaults);
        this.jobDirectoryManager = jobDirectoryManager;
        this.tvShowRepository = tvShowRepository;
        this.jobQueueService = jobQueueService;
    }

    @Override
    public String saveOrAppend(final InputStream inputStream,
                               final String fileName,
                               final long startByte,
                               final long totalFileBytes,
                               final String contentType) throws IOException {
        return jobDirectoryManager.appendStreamToFile(fileName, startByte, totalFileBytes, inputStream);
    }


    @Override
    public void process(List<String> jobIds) {
        for (String tvShowName : jobIds) {
            final File tvShowFolder = jobDirectoryManager.getJobDirectory(tvShowName);
            final File tvShowZip = Path.of(tvShowFolder.getPath(), tvShowName).toFile();
            try {
                final FileInputStream tvShowZipStream = new FileInputStream(tvShowZip);
                final File tempFolder = jobDirectoryManager.unzipAlbum(tvShowZipStream, tvShowName);
                writeMetadataFor(tempFolder, tvShowName);
                if (tvShowName == null) {
                    throw new IncorrectFileFormatException("Error accessing Filename");
                }
                if (isValidTvShowFolder(tempFolder)) {
                    final Job job = new Job(tvShowName, () -> processTvShow(tempFolder));
                    jobQueueService.addJob(job);
                } else {
                    throw new IncorrectFileFormatException(tvShowName, "Unable to find at least one video file in every season");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    private void processTvShow(File tempFolder) {
        try {
            final Map<String, String> metadata = readMetadataFileFor(tempFolder);
            final TvShow tvShow = parseTvShow(tempFolder, metadata.get(MEDIA_NAME_METADATA_KEY));
            tvShowRepository.saveTvShow(tvShow);
            log.info("Saved TV Show [{}]", tvShow.name());
        } catch (IOException e) {
            log.error("There was a problem processing TV Show with Job ID [{}] {}", tempFolder.getName(), e.getMessage());
            log.error("Exception", e);
        }
        try {
            cleanupTvShowFolder(tempFolder);
        } catch (IOException e) {
            log.error("There was a problem cleaning up TV Show with Job ID [{}] {}", tempFolder.getName(), e.getMessage());
            log.error("Exception", e);
        }
    }

    private void cleanupTvShowFolder(final File tempFolder) throws IOException {
        final File[] files = safeListDirectory(tempFolder);
        for (File file : files) {
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                FileUtils.delete(file);
            }
        }
    }

    private TvShow parseTvShow(final File tempTvShowFolder, final String tvShowName) {
        final File[] seasonFolders = safeListDirectory(tempTvShowFolder);
        final TvShow tvShow = new TvShow(tvShowName.replaceFirst("\\..+$", ""), seasonFolders.length + 1);
        log.info("Starting save process for TvShow [{}], found [{}] season folders", tvShowName, seasonFolders.length);
        for (final File seasonFolder : seasonFolders) {
            if (!seasonFolder.isDirectory()) {
                log.info("Skipping non-directory file [{}] in TV Show processing for [{}]", seasonFolder.getName(), tempTvShowFolder.getName());
                continue;
            }
            final File[] episodeFiles = safeListDirectory(seasonFolder);
            for (int e = 0; e < episodeFiles.length; e++) {
                final File episodeFile = episodeFiles[e];
                log.info("Parsing info from episode [{}]", episodeFile.getName());
                final int currentSeasonNumber = parseSeasonNumberFromEpisode(episodeFile.getName())
                        .orElse(parseSeasonNumberFromFolder(seasonFolder.getName())
                                .orElse(-1));
                log.info("found season [{}] from episode name", currentSeasonNumber);
                final Season existingSeason = tvShow.getSeason(currentSeasonNumber);
                final Season season = existingSeason == null ? new Season(currentSeasonNumber, episodeFiles.length) : existingSeason;
                if (existingSeason == null) tvShow.addSeason(season, season.seasonNumber());
                final Episode episode = parseEpisode(tvShow.name(), season, episodeFile);
                log.info("Adding episode [{}] to season [{}]", episode, currentSeasonNumber);
                season.addEpisode(e, episode);
            }
        }
        return tvShow;
    }

    private Episode parseEpisode(final String tvShowName,
                                 final Season season,
                                 final File episodeFile) {
        final String episodeNameRaw = episodeFile.getName();
        log.info("Parsing info from episode [{}]", episodeNameRaw);
        final int episodeNumber = parseEpisodeNumber(episodeNameRaw).orElse(-1);
        final Episode episode = new Episode(episodeNumber);
        final String fileExtension = parseExtension(episodeNameRaw);
        final String episodeName = cleanEpisodeName(tvShowName, season, episodeNumber, fileExtension);
        episode.setFile(episodeFile);
        episode.setExtension(fileExtension);
        episode.setName(episodeName);
        log.info("Parsed following information [{}]", episode);
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

}
