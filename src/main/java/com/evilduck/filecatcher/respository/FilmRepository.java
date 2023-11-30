package com.evilduck.filecatcher.respository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

@Slf4j
@Repository
public class FilmRepository implements MediaFileRepository {

    private final String filmsDirectory;

    public FilmRepository(@Value("${directories.films}") String filmsDirectory) {
        this.filmsDirectory = filmsDirectory;
    }

    @Override
    public Optional<String> save(Resource resource) {
        try {
            final byte[] fileBytes = resource.getContentAsByteArray();
            final String cleansedFilename = Optional.ofNullable(resource.getFilename())
                    .map(name -> name.replaceAll("\\s+", "_"))
                    .orElseThrow(() -> new NullPointerException("Resource filename is null"));
            final File outputFile = new File(filmsDirectory + cleansedFilename);
            if (outputFile.exists()) throw new FileAlreadyExistsException("File already exists");
            FileUtils.touch(outputFile);
            FileUtils.writeByteArrayToFile(outputFile, fileBytes);
        } catch (IOException e) {
            log.error("Something went wrong with writing the file: [{}], message: [{}]", resource.getFilename(), e.getMessage());
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }
}
