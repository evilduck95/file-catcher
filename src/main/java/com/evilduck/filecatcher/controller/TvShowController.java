package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.request.JobProcessingRequest;
import com.evilduck.filecatcher.service.TvShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/tv-show")
public class TvShowController {

    private final TvShowService tvShowService;

    public TvShowController(TvShowService tvShowService) {
        this.tvShowService = tvShowService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadTvShow(@RequestParam("file") final MultipartFile file) throws IOException {
        final String jobId = tvShowService.save(file.getResource(), file.getContentType());
        final FileUploadResponse fileUploadResponse = new FileUploadResponse("success", jobId);
        return ResponseEntity.ok(fileUploadResponse);
    }

    @PatchMapping("/process")
    public ResponseEntity<Void> processTvShow(@RequestBody final JobProcessingRequest jobProcessingRequest) {
        tvShowService.process(jobProcessingRequest.jobIds());
        return ResponseEntity.noContent().build();
    }

}
