package com.evilduck.filecatcher.dto;

import java.util.List;

public record JobStatusResponse(String status, String message, List<JobFileError> errors) {

}
