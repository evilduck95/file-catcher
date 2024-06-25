package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmRepositoryTest {

    @Value("classpath:file-that-already-exists.ext")
    private ClassPathResource fileThatAlreadyExists;

    private FilmRepository filmRepository;

    @BeforeEach
    void setUp() throws IOException {
        fileThatAlreadyExists = new ClassPathResource("file-that-already-exists.ext");
        final FileDefaults fileDefaults = new FileDefaults();
        fileDefaults.setDelimiter('-');
        filmRepository = new FilmRepository(fileDefaults, fileThatAlreadyExists.getFile().getParent() + "\\");
    }

    @Test
    void shouldSave() throws IOException {
        final MockedStatic<Files> filesMockedStatic = mockStatic(Files.class);
        final Film mockFilm = mock(Film.class);
        Mockito.when(mockFilm.getName()).thenReturn("name");
        Mockito.when(mockFilm.getExtension()).thenReturn("ext");
        final File mockFile = mock(File.class);
        Mockito.when(mockFilm.getFile()).thenReturn(mockFile);
        final Path mockPath = mock(Path.class);
        Mockito.when(mockFile.toPath()).thenReturn(mockPath);
        filmRepository.save(mockFilm);
        verify(mockFilm, times(1)).getFile();
        filesMockedStatic.close();
    }

    @Test
    void shouldThrowIfFileAlreadyExists() throws IOException {
        final Film mockFilm = mock(Film.class);
        final String[] nameExtension = fileThatAlreadyExists.getFile().getName().split("\\.");
        Mockito.when(mockFilm.getName()).thenReturn(nameExtension[0]);
        Mockito.when(mockFilm.getExtension()).thenReturn(nameExtension[1]);
        assertThrows(FileAlreadyExistsException.class, () -> filmRepository.save(mockFilm));
    }
}