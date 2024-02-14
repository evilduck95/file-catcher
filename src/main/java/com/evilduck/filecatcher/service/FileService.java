package com.evilduck.filecatcher.service;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Objects;


public abstract class FileService {

    private final String[] expectedContentTypes;

    protected FileService(String... expectedContentTypes) {
        this.expectedContentTypes = expectedContentTypes;
    }

    public abstract void save(Resource media, String contentType) throws IOException;

    boolean correctContentType(final String contentType) {
        if (contentType == null) return false;
        for (String type : expectedContentTypes) {
            if (Objects.equals(type, contentType)) return true;
        }
        return false;
    }

}
