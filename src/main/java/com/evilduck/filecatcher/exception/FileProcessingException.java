package com.evilduck.filecatcher.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileProcessingException extends RuntimeException {

    private final String mediaName;
    private final String errorMessage;

    public FileProcessingException(String mediaName, String errorMessage) {
        this.mediaName = mediaName;
        this.errorMessage = errorMessage;
    }
}

