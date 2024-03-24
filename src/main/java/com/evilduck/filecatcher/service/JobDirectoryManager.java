package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.exception.FileSaveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
public class JobDirectoryManager {

    private final String tempDirectory;

    public JobDirectoryManager(@Value("${directories.temp-directory}") String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }


    public File getJobDirectory(final String jobId) {
        return Path.of(tempDirectory, jobId).toFile();
    }

    public File tempStoreResource(final Resource resource) throws IOException {
        final UUID jobId = UUID.randomUUID();
        final Path workingDirectoryPath = Files.createDirectories(Path.of(tempDirectory + jobId));
        if (resource.getFilename() == null) throw new FileSaveException("File has no filename");
        final Path outputFile = workingDirectoryPath.resolve(resource.getFilename());
        Files.copy(resource.getInputStream(), outputFile);
        return workingDirectoryPath.toFile();
    }

    /**
     * Please don't ever touch this :3 it currently works!
     * Takes a Zip archive and extracts it, as is to a temporary directory.
     *
     * @param resource A Zip archive.
     * @return Path to the temporary directory containing the extracted archive.
     * @throws IOException when something goes wrong reading the Zip archive or creating a directory.
     */
    public File unzipAlbum(final Resource resource) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(resource.getInputStream());
        ZipEntry nextEntry = zipInputStream.getNextEntry();
        final UUID jobId = UUID.randomUUID();
        log.info("Unzipping Album, Job ID [{}]", jobId);
        final Path workingDirectoryPath = Files.createDirectories(Path.of(tempDirectory + jobId));
        while (nextEntry != null) {
            final File newFile = createNewFile(workingDirectoryPath.toFile(), nextEntry);
            if (nextEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Unable to create directory: " + newFile);
                }
            } else {
                final File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Unable to create directory: " + parent);
                }
                Files.copy(zipInputStream, newFile.toPath());
            }
            nextEntry = zipInputStream.getNextEntry();
        }
        return workingDirectoryPath.toFile();
    }

    private static File createNewFile(final File directory, final ZipEntry zipEntry) throws IOException {
        final File destinationFile = new File(directory, zipEntry.getName());
        final String canonRequestedDirPath = directory.getCanonicalPath();
        final String canonOutputFilePath = destinationFile.getCanonicalPath();
        if (!canonOutputFilePath.startsWith(canonRequestedDirPath + File.separator)) {
            log.error("ZIP Slip protection activated! Job directory [{}]", directory.getPath());
            throw new IOException("Zip Entry is outside of target directory: " + zipEntry.getName());
        } else {
            return destinationFile;
        }
    }

}
