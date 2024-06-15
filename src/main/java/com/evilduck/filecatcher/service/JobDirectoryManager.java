package com.evilduck.filecatcher.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
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

    public String tempStoreStreamAsFile(final String fileName,
                                        final InputStream inputStream) throws IOException {
        final Path workingDirectoryPath = Files.createDirectories(Path.of(tempDirectory + fileName));
        final String outputFilePath = workingDirectoryPath.resolve(fileName).toString();
        try (final FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
            IOUtils.copy(inputStream, outputStream);
            return fileName;
        }
    }

    public String appendStreamToFile(final String fileName,
                                     final int startByte,
                                     final int totalFileBytes,
                                     final InputStream inputStream) throws IOException {
        final Path workingDirectoryPath = Files.createDirectories(Path.of(tempDirectory + fileName));
        final String outputFilePath = workingDirectoryPath.resolve(fileName).toString();
        final File outputFile = new File(outputFilePath);
        try (final RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile, "rw")) {
            randomAccessFile.setLength(totalFileBytes);
            randomAccessFile.seek(startByte);
            final byte[] chunkBytes = inputStream.readAllBytes();
            randomAccessFile.write(chunkBytes);
        }
        return fileName;
//        try (final FileOutputStream outputStream = new FileOutputStream(outputFilePath, true)) {
//            IOUtils.copy(inputStream, outputStream);
//            return fileName;
//        }
    }

    /**
     * Please don't ever touch this :3 it currently works!
     * Takes a Zip archive and extracts it as is, to a temporary directory.
     *
     * @param inputStream A Zip archive stream.
     * @return Path to the temporary directory containing the extracted archive.
     * @throws IOException when something goes wrong reading the Zip archive or creating a directory.
     */
    public File unzipAlbum(final InputStream inputStream) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);
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
