package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.JobStatusResponse;
import com.evilduck.filecatcher.service.JobQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    private final JobQueueService jobQueueService;

    public JobController(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable final String jobId) {
        final JobStatusResponse jobStatusResponse = jobQueueService.checkJobResult(jobId);
        return ResponseEntity.ok().body(jobStatusResponse);
    }

}
