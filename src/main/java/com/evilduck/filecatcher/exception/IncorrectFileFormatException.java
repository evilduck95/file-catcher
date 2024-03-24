package com.evilduck.filecatcher.exception;

public class IncorrectFileFormatException extends FileProcessingException {

    public IncorrectFileFormatException(String mediaName, String errorMessage) {
        super(mediaName, errorMessage);
    }
}
