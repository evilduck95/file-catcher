package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

@Slf4j
public class FilmRepository extends FileRepository {


    public FilmRepository(final FileDefaults fileDefaults,
                          final String directory) {
        super(fileDefaults, directory);
    }

    public void save(final Film film) throws IOException {
        final String finalFileName;
        if(film.getReleaseYear() != null){
            finalFileName = String.format("%s(%4d).%s",
                    film.getName().endsWith(String.valueOf(fileDefaults.getDelimiter())) ? film.getName() : film.getName() + fileDefaults.getDelimiter(),
                    film.getReleaseYear().getValue(),
                    film.getExtension());
        }
        else{
            finalFileName = String.format("%s.%s",
                    film.getName().endsWith(String.valueOf(fileDefaults.getDelimiter())) ? film.getName() : film.getName() + fileDefaults.getDelimiter(),
                    film.getExtension());
        }
        final File outputFile = new File(directory + finalFileName);
        if (outputFile.exists())
            throw new FileAlreadyExistsException("File " + outputFile.getName() + " already exists");
        Files.copy(film.getFile().toPath(), outputFile.toPath());
        log.info("Saved file at [{}]", outputFile.getPath());
    }
}
