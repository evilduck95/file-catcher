package com.evilduck.filecatcher.respository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

@Slf4j
public class FileRepository implements MediaFileRepository {

    private final String directory;

    public FileRepository(final String directory) {
        this.directory = directory;
    }

    @Override
    public Optional<String> save(Resource resource) {
        try {
            final byte[] fileBytes = resource.getContentAsByteArray();
            final String cleansedFilename = Optional.ofNullable(resource.getFilename())
                    .map(name -> name.replaceAll("\\s+", "_"))
                    .orElseThrow(() -> new NullPointerException("Resource filename is null"));
            final File outputFile = new File(directory + cleansedFilename);
            if (outputFile.exists()) throw new FileAlreadyExistsException("File already exists");
            FileUtils.touch(outputFile);
            FileUtils.writeByteArrayToFile(outputFile, fileBytes);
            log.info("Saved file at [{}]", outputFile.getPath());
        } catch (IOException e) {
            log.error("Something went wrong with writing the file: [{}], message: [{}]", resource.getFilename(), e.getMessage());
            return Optional.of(e.getMessage());
        }
        return Optional.empty();
    }
}
