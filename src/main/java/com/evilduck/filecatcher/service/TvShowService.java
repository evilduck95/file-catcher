package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.model.TvShow;
import com.evilduck.filecatcher.respository.FileRepository;
import com.evilduck.filecatcher.respository.TvShowRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TvShowService extends FileService {

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("(.*)([s|S][0-9]{2})([e|E][0-9]{2}).*\\.(.+)");
    private final ZipManager zipManager;
    private final FileRepository tvShowRepository;

    protected TvShowService(final ZipManager zipManager,
                            final FileRepository tvShowRepository) {
        super("application/zip");
        this.zipManager = zipManager;
        this.tvShowRepository = tvShowRepository;
    }

    private void parseTvShow(final File tempTvShowFolder) {
        // TODO: Parse tv show, seasons and episodes.
        final File[] seasons = safeListDirectory(tempTvShowFolder);
        for (File seasonFolder : seasons) {
            final File[] episodes = safeListDirectory(seasonFolder);
            for (File episodeFile : episodes) {
                final String episodeNameRaw = episodeFile.getName();
                final Matcher episodeNameMatcher = FILE_NAME_PATTERN.matcher(episodeNameRaw);
                // TODO: This could be anything or nothing, usually a show name, might need modifying.
                if(episodeNameMatcher.find()){
                    final String episodeName = episodeNameMatcher.group(1);
                    final String seasonNumber = episodeNameMatcher.group(2);
                    final String episodeNumber = episodeNameMatcher.group(3);
                    final String fileExtension = episodeNameMatcher.group(4);
                    final String outputFileName = String.format("%1$s_S%2$se%3$s.%4$s", episodeName, seasonNumber, episodeNumber, fileExtension);
                }
            }
        }
    }

    private File[] safeListDirectory(final File directory) {
        final File[] list = directory.listFiles();
        if (list == null) throw new RuntimeException("Unable to list directory [" + directory.getPath() + ']');
        return list;
    }

    @Override
    public void save(final Resource media, final String contentType) throws IOException {
        if (correctContentType(contentType)) {
            final File tempFolder = zipManager.unzipAlbum(media);
            if (media.getFilename() == null) throw new IncorrectFileFormatException("Error accessing Filename");
            if (isValidTvShowFolder(tempFolder)) {
                tvShowRepository.save(tempFolder, media.getFilename().replaceFirst("[.].+", ""));
            } else {
                throw new IncorrectFileFormatException("Unable to find at least one video file in every season");
            }
        } else {
            throw new IncorrectFileFormatException("File is not a ZIP archive");
        }
    }

    private boolean isValidTvShowFolder(final File folder) {
        final String[] seasons = folder.list();
        if (seasons == null) return false;
        for (String season : seasons) {
            final File seasonFolder = Path.of(folder.getPath(), season).toFile();
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
