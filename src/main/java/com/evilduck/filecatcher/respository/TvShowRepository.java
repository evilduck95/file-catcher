package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.model.Episode;
import com.evilduck.filecatcher.model.Season;
import com.evilduck.filecatcher.model.TvShow;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TvShowRepository {

    private final String tvShowDirectory;
    private final FileDefaults fileDefaults;

    public TvShowRepository(@Value("${directories.tv-shows}") String tvShowDirectory,
                            FileDefaults fileDefaults) {
        this.tvShowDirectory = tvShowDirectory;
        this.fileDefaults = fileDefaults;
    }

    private void saveTvShow(final TvShow tvShow) throws IOException {
        final List<Season> seasons = tvShow.seasons();
        for (Season season : seasons) {
            saveSeason(tvShow.name(), season);
        }
    }

    private void saveSeason(final String tvShowName,
                            final Season season) throws IOException {
        final List<Episode> episodes = season.episodes();
        for (Episode episode : episodes) {
            saveEpisode(tvShowName, season.seasonNumber(), episode);
        }
    }

    private void saveEpisode(final String tvShowName,
                             final int seasonNumber,
                             final Episode episode) throws IOException {
        // Rename Episode as per our naming conventions
        final char delimiter = fileDefaults.getDelimiter();
        final File originalFile = episode.getFile();
        final String episodeFileName = String.format("%s%sS%02dE%02d.%s",
                tvShowName,
                delimiter,
                seasonNumber,
                episode.getEpisodeNumber(),
                episode.getExtension());
        final String seasonFolderName = String.format("Season%s%02d/", delimiter, seasonNumber);
        final File episodeOutputFile = new File(tvShowDirectory + seasonFolderName + episodeFileName);
        FileUtils.createParentDirectories(episodeOutputFile);
        FileUtils.writeByteArrayToFile(episodeOutputFile, FileUtils.readFileToByteArray(originalFile));
    }

}
