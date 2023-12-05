package com.evilduck.filecatcher.service;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

import java.util.Optional;


public abstract class FileService {

    private final String expectedContentType;

    protected FileService(String expectedContentType) {
        this.expectedContentType = expectedContentType;
    }

    public abstract Optional<String> save(Resource media, @Nullable String contentType);

    boolean correctContentType(@Nullable final String contentType) {
        return Optional.ofNullable(contentType).map(type -> type.startsWith(expectedContentType)).orElse(false);
    }

}
