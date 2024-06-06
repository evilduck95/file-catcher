package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class FileService {

    protected FileDefaults fileDefaults;
    private final String[] expectedContentTypes;

    protected FileService(FileDefaults fileDefaults, String... expectedContentTypes) {
        this.fileDefaults = fileDefaults;
        this.expectedContentTypes = expectedContentTypes;
    }

    public abstract String save(InputStream inputStream, String fileName, String contentType) throws IOException;

    public abstract void process(List<String> jobIds);

    boolean correctContentType(final String contentType) {
        if (contentType == null) return false;
        for (String type : expectedContentTypes) {
            if (contentType.contains(type)) return true;
        }
        return false;
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

    protected File getMetadataFileFor(File tempFolder) {
        return tempFolder.toPath().resolve("metadata").toFile();
    }

}
