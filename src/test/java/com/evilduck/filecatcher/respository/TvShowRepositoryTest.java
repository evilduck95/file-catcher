package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.model.Episode;
import com.evilduck.filecatcher.model.Season;
import com.evilduck.filecatcher.model.TvShow;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class TvShowRepositoryTest {

    private static final String TV_SHOW_NAME = "name";

    @Value("classpath:directory-that-exists/file-that-already-exists.ext")
    private ClassPathResource fileThatAlreadyExists;

    private TvShowRepository tvShowRepository;

    @BeforeEach
    void setUp() throws IOException {
        fileThatAlreadyExists = new ClassPathResource("directory-that-exists/file-that-already-exists.ext");
        final FileDefaults fileDefaults = new FileDefaults();
        fileDefaults.setDelimiter('-');
        tvShowRepository = new TvShowRepository(fileDefaults, fileThatAlreadyExists.getFile().getParent() + "\\..\\");
    }

    @AfterEach
    void tearDown() throws IOException {
        final Path tvShowFolder = Path.of(fileThatAlreadyExists.getFile().getParent(), TV_SHOW_NAME);
        Files.deleteIfExists(tvShowFolder);
    }

    @Test
    void shouldSave() throws IOException {
        final MockedStatic<FileUtils> fileUtilsMockedStatic = mockStatic(FileUtils.class);
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
        final Episode episode = mock(Episode.class);
        when(episode.getFile()).thenReturn(fileThatAlreadyExists.getFile());
        final Season season = mock(Season.class);
        when(season.episodes()).thenReturn(new Episode[]{episode});
        final TvShow tvShow = mock(TvShow.class);
        when(tvShow.seasons()).thenReturn(new Season[]{season});
        when(tvShow.name()).thenReturn(TV_SHOW_NAME);
        tvShowRepository.saveTvShow(tvShow);
        verify(episode, times(1)).getFile();
        filesMockedStatic.close();
        fileUtilsMockedStatic.close();
    }

    @Test
    void shouldThrowWhenAlreadyExists() throws IOException {
        final MockedStatic<FileUtils> fileUtilsMockedStatic = mockStatic(FileUtils.class);
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
        final Episode episode = mock(Episode.class);
        when(episode.getFile()).thenReturn(fileThatAlreadyExists.getFile());
        final Season season = mock(Season.class);
        when(season.episodes()).thenReturn(new Episode[]{episode});
        final TvShow tvShow = mock(TvShow.class);
        when(tvShow.seasons()).thenReturn(new Season[]{season});
        when(tvShow.name()).thenReturn("directory-that-exists");
        assertThrows(FileAlreadyExistsException.class, () -> tvShowRepository.saveTvShow(tvShow));
        filesMockedStatic.close();
        fileUtilsMockedStatic.close();
    }
}