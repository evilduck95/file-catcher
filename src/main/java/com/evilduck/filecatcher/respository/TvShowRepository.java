package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.model.Episode;
import com.evilduck.filecatcher.model.Season;
import com.evilduck.filecatcher.model.TvShow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

@Slf4j
public class TvShowRepository extends FileRepository {

    public TvShowRepository(FileDefaults fileDefaults,
                            String directory) {
        super(fileDefaults, directory);
    }

    public void saveTvShow(final TvShow tvShow) throws IOException {
        final String morallyCorrectTvShowName = tvShow.name()
                .toLowerCase()
                .replaceAll("\\s+", String.valueOf(fileDefaults.getDelimiter()));
        log.info("Saving TV Show [{}]", morallyCorrectTvShowName);
        final File tvShowFolder = Path.of(directory, morallyCorrectTvShowName).toFile();
        if (tvShowFolder.exists())
            throw new FileAlreadyExistsException("TV Show already exists with name [" + morallyCorrectTvShowName + ']');
        final Season[] seasons = tvShow.seasons();
        for (Season season : seasons) {
            if (season == null) continue;
            saveSeason(morallyCorrectTvShowName, season);
        }
    }

    private void saveSeason(final String tvShowName,
                            final Season season) throws IOException {
        final Episode[] episodes = season.episodes();
        for (Episode episode : episodes) {
            if (episode == null) continue;
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
        final String seasonFolderName = String.format("season%s%02d/", delimiter, seasonNumber);
        final Path episodeOutputPath = Path.of(directory, tvShowName, seasonFolderName, episodeFileName);
        final File episodeOutputFile = episodeOutputPath.toFile();
        FileUtils.createParentDirectories(episodeOutputFile);
        FileUtils.writeByteArrayToFile(episodeOutputFile, FileUtils.readFileToByteArray(originalFile));
    }

}
