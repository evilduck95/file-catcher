package com.evilduck.filecatcher.exception;

public class FileSaveException extends FileProcessingException {
    public FileSaveException(String mediaName, String errorMessage) {
        super(mediaName, errorMessage);
    }
}
