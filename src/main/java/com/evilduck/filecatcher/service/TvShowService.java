package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.model.Episode;
import com.evilduck.filecatcher.model.TvShow;
import com.evilduck.filecatcher.respository.FileRepository;
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

    private static final Pattern FILE_NAME_PATTERN = Pattern.compile("(.*)(s[0-9]{2})(e[0-9]{2}).*\\.(.+)", Pattern.CASE_INSENSITIVE);
    private final ZipManager zipManager;
    private final FileRepository tvShowRepository;

    protected TvShowService(final ZipManager zipManager,
                            final FileRepository tvShowRepository) {
        super("application/zip");
        this.zipManager = zipManager;
        this.tvShowRepository = tvShowRepository;
    }

    private TvShow parseTvShow(final File tempTvShowFolder, final String tvShowName) {
        final File[] seasonFolders = safeListDirectory(tempTvShowFolder);
        final TvShow tvShow = new TvShow(tvShowName);
        for (int s = 0; s < seasonFolders.length; s++) {
            File seasonFolder = seasonFolders[s];
            final File[] episodeFiles = safeListDirectory(seasonFolder);
            for (int e = 0; e < episodeFiles.length; e++) {
                final File episodeFile = episodeFiles[e];
                final String episodeNameRaw = episodeFile.getName();
                final Episode episode = new Episode(e);
                episode.setFile(episodeFile);
                episode.setName(episodeNameRaw);
                tvShow.addEpisode(s, e, episode);
            }
        }
        return tvShow;
    }

    private String cleanEpisodeName(final String episodeNameRaw) {
        final Matcher episodeNameMatcher = FILE_NAME_PATTERN.matcher(episodeNameRaw);
        // TODO: This could be anything or nothing, usually a show name, might need modifying.
        if(episodeNameMatcher.find()){
            final String episodeName = episodeNameMatcher.group(1);
            final String seasonNumber = episodeNameMatcher.group(2);
            final String episodeNumber = episodeNameMatcher.group(3);
            final String fileExtension = episodeNameMatcher.group(4);
            final String outputFileName = String.format("%1$s_S%2$se%3$s.%4$s", episodeName, seasonNumber, episodeNumber, fileExtension);

        }
        return null;
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
            final String mediaName = media.getFilename();
            if (mediaName == null) throw new IncorrectFileFormatException("Error accessing Filename");
            if (isValidTvShowFolder(tempFolder)) {
                final TvShow tvShow = parseTvShow(tempFolder, mediaName);
                tvShowRepository.save(tempFolder, mediaName.replaceFirst("[.].+", ""));
            } else {
                throw new IncorrectFileFormatException("Unable to find at least one video file in every season");
            }
        } else {
            throw new IncorrectFileFormatException("File is not a ZIP archive");
        }
    }

//    @Override
//    public void save(final Resource media, final String contentType) throws IOException {
//        if (correctContentType(contentType)) {
//            final File tempFolder = zipManager.unzipAlbum(media);
//            if (media.getFilename() == null) throw new IncorrectFileFormatException("Error accessing Filename");
//            if (isValidTvShowFolder(tempFolder)) {
//                tvShowRepository.save(tempFolder, media.getFilename().replaceFirst("[.].+", ""));
//            } else {
//                throw new IncorrectFileFormatException("Unable to find at least one video file in every season");
//            }
//        } else {
//            throw new IncorrectFileFormatException("File is not a ZIP archive");
//        }
//    }

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
