package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.request.JobProcessingRequest;
import com.evilduck.filecatcher.service.FilmService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/film")
@CrossOrigin(origins = {"http://localhost:3000", "https://evilduck95.net"})
public class FilmController extends FileDownloadingController {

    public FilmController(FilmService filmService) {
        super(filmService);
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFilm(final HttpServletRequest request) throws IOException {
        return handleUpload(request);
    }

    @PatchMapping("/process")
    public ResponseEntity<Void> processFilm(@RequestBody final JobProcessingRequest jobProcessingRequest) {
        return processMedia(jobProcessingRequest);
    }

}
