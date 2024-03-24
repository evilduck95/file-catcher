package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileErrorResponse;
import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GeneralControllerAdvice {

    @ExceptionHandler(IncorrectFileFormatException.class)
    public ResponseEntity<FileErrorResponse> handleIncorrectFileFormatException(final IncorrectFileFormatException exception) {
        final FileErrorResponse fileErrorResponse = new FileErrorResponse(exception.getMediaName(), exception.getMessage());
        return ResponseEntity.badRequest().body(fileErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FileErrorResponse> handleGenericException(final Exception exception) {
        final FileErrorResponse fileErrorResponse = new FileErrorResponse("Unknown", exception.getMessage());
        return ResponseEntity.internalServerError().body(fileErrorResponse);
    }

}
