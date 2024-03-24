package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.dto.JobStatusResponse;
import com.evilduck.filecatcher.model.Job;

public interface JobQueueService {

    void addJob(final Job job);

    JobStatusResponse checkJobResult(final String jobId);

}
