package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.request.JobProcessingRequest;
import com.evilduck.filecatcher.service.TvShowService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/tv-show")
@CrossOrigin(origins = {"http://localhost:3000", "https://evilduck95.net"})
public class TvShowController extends FileDownloadingController {


    public TvShowController(TvShowService tvShowService) {
        super(tvShowService);
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadTvShow(final HttpServletRequest request) throws IOException, ServletException {
        return handleUpload(request);
    }

    @PatchMapping("/process")
    public ResponseEntity<Void> processTvShow(@RequestBody final JobProcessingRequest jobProcessingRequest) {
        return processMedia(jobProcessingRequest);
    }

}
