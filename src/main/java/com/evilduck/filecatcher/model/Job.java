package com.evilduck.filecatcher.model;

import com.evilduck.filecatcher.respository.JobRunner;

public record Job(String jobId, JobRunner jobRunner) {

}
