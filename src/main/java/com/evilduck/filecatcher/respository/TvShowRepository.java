package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.model.Episode;
import com.evilduck.filecatcher.model.Season;
import com.evilduck.filecatcher.model.TvShow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class TvShowRepository extends FileRepository {

    public TvShowRepository(FileDefaults fileDefaults, String directory) {
        super(fileDefaults, directory);
    }

    private void saveTvShow(final TvShow tvShow) throws IOException {
        final Season[] seasons = tvShow.seasons();
        for (Season season : seasons) {
            saveSeason(tvShow.name(), season);
        }
    }

    private void saveSeason(final String tvShowName,
                            final Season season) throws IOException {
        final Episode[] episodes = season.episodes();
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
        final File episodeOutputFile = new File(directory + seasonFolderName + episodeFileName);
        FileUtils.createParentDirectories(episodeOutputFile);
        FileUtils.writeByteArrayToFile(episodeOutputFile, FileUtils.readFileToByteArray(originalFile));
    }

}
