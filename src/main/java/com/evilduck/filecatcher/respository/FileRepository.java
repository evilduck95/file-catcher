package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.exception.FileSaveException;
import com.evilduck.filecatcher.model.Film;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

@Slf4j
public class FileRepository implements MediaFileRepository {

    FileDefaults fileDefaults;
    final String directory;

    public FileRepository(final String directory) {
        this.directory = directory;
    }

    @Override
    public String save(final Resource resource) {
        try {
            final byte[] fileBytes = resource.getContentAsByteArray();
            final String cleansedFilename = Optional.ofNullable(resource.getFilename())
                    .map(name -> name.replaceAll(fileDefaults.getCleanseRegex(), String.valueOf(fileDefaults.getDelimiter())))
                    .orElseThrow(() -> new NullPointerException("Resource filename is null"));
            final File outputFile = new File(directory + cleansedFilename);
            if (outputFile.exists()) throw new FileAlreadyExistsException("File already exists");
            FileUtils.touch(outputFile);
            FileUtils.writeByteArrayToFile(outputFile, fileBytes);
            log.info("Saved file at [{}]", outputFile.getPath());
            return outputFile.getPath();
        } catch (IOException e) {
            log.error("Something went wrong writing the file: [{}], message: [{}]", resource.getFilename(), e.getMessage());
            throw new FileSaveException(e.getMessage());
        }
    }


    @Override
    public String save(final File folder, final String name) throws IOException {
        final File outputDir = new File(directory + name + "/");
        if(folder.isDirectory()) {
            if (outputDir.exists()) throw new FileAlreadyExistsException("Directory already exists");
            FileUtils.moveDirectory(folder, outputDir);
        } else {
            throw new RuntimeException("File is not directory");
        }
        return outputDir.getPath();
    }
}
