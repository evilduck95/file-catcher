package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.BulkJobStatusResponse;
import com.evilduck.filecatcher.dto.JobStatusResponse;
import com.evilduck.filecatcher.service.JobQueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job")
@CrossOrigin(origins = {"http://localhost:3000", "https://evilduck95.net"})
public class JobController {

    private final JobQueueService jobQueueService;

    public JobController(JobQueueService jobQueueService) {
        this.jobQueueService = jobQueueService;
    }

    @PostMapping("/bulk-check")
    public ResponseEntity<BulkJobStatusResponse> getJobStatuses(@RequestBody final List<String> jobIds) {
        final List<JobStatusResponse> statuses = jobIds.stream().map(jobQueueService::checkJobResult).toList();
        return ResponseEntity.ok().body(new BulkJobStatusResponse(statuses));
    }

    @GetMapping("/check/{jobId}")
    public ResponseEntity<JobStatusResponse> getJobStatus(@PathVariable final String jobId) {
        return ResponseEntity.ok(jobQueueService.checkJobResult(jobId));
    }

}
