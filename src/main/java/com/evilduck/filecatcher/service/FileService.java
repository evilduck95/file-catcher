package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class FileService {

    protected FileDefaults fileDefaults;
    private final String[] expectedContentTypes;

    protected FileService(FileDefaults fileDefaults, String... expectedContentTypes) {
        this.fileDefaults = fileDefaults;
        this.expectedContentTypes = expectedContentTypes;
    }

    public abstract void save(Resource media, String contentType) throws IOException;

    boolean correctContentType(final String contentType) {
        if (contentType == null) return false;
        for (String type : expectedContentTypes) {
            if (contentType.contains(type)) return true;
        }
        return false;
    }

    String cleanseName(final String filename){
        return filename.replaceAll(fileDefaults.getCleanseRegex(), String.valueOf(fileDefaults.getDelimiter()));
    }

    String parseExtension(final String filename){
        final Pattern extensionPattern = Pattern.compile(fileDefaults.getExtensionRegex());
        final Matcher extensionMatcher = extensionPattern.matcher(filename);
        if(extensionMatcher.find()){
            return extensionMatcher.group(2);
        }
        return "";
    }

}
