package com.evilduck.filecatcher.dto;

import java.util.List;

public record JobStatusResponse(String jobId, String status, String message, List<JobFileError> errors) {

}
