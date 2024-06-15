package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class FileService {

    protected static final String MEDIA_NAME_METADATA_KEY = "name";

    protected FileDefaults fileDefaults;
    private final String[] expectedContentTypes;

    protected FileService(FileDefaults fileDefaults, String... expectedContentTypes) {
        this.fileDefaults = fileDefaults;
        this.expectedContentTypes = expectedContentTypes;
    }

    public abstract String save(InputStream inputStream, String fileName, String contentType) throws IOException;

    public abstract String saveOrAppend(InputStream inputStream, String fileName, int startByte, int totalFileBytes, String contentType) throws IOException;


    public abstract void process(List<String> jobIds);

    boolean correctContentType(final String contentType) {
        return true;
    }

    protected String cleanseName(final String filename) {
        return filename.replaceAll(fileDefaults.getCleanseRegex(), String.valueOf(fileDefaults.getDelimiter()));
    }

    protected String parseExtension(final String filename) {
        final Pattern extensionPattern = Pattern.compile(fileDefaults.getExtensionRegex());
        final Matcher extensionMatcher = extensionPattern.matcher(filename);
        if(extensionMatcher.find()){
            return extensionMatcher.group(2);
        }
        return "";
    }

    protected Map<String, String> readMetadataFileFor(File tempFolder) {
        try (final BufferedReader reader = new BufferedReader(new FileReader(tempFolder.toPath().resolve("metadata").toFile()))) {
            final List<String> lines = reader.lines().toList();
            final Map<String, String> metadataMap = new HashMap<>();
            lines.forEach(l -> {
                final String[] keyValue = l.split(":");
                metadataMap.put(keyValue[0], keyValue[1]);
            });
            return metadataMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeMetadataFor(File tempFolder, String mediaName) {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFolder.toPath().resolve("metadata").toFile()))) {
            writer.write(String.format("%s:%s", MEDIA_NAME_METADATA_KEY, mediaName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    protected File getMetadataFileFor(File tempFolder) {
        return tempFolder.toPath().resolve("metadata").toFile();
    }

}
