package com.evilduck.filecatcher.dto;

import java.util.List;

public record BulkJobStatusResponse(List<JobStatusResponse> jobStates) {

}
