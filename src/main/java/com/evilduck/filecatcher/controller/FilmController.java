package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.request.JobProcessingRequest;
import com.evilduck.filecatcher.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/film")
@CrossOrigin(origins = {"http://localhost:3000", "http://evilduck95.net"})
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFilm(@RequestParam("file") final MultipartFile file) throws IOException {
        final String jobId = filmService.save(file.getResource(), file.getContentType());
        final FileUploadResponse fileUploadResponse = new FileUploadResponse("success", jobId);
        return ResponseEntity.ok(fileUploadResponse);
    }

    @PatchMapping("/process")
    public ResponseEntity<Void> processFilm(@RequestBody final JobProcessingRequest jobProcessingRequest) {
        filmService.process(jobProcessingRequest.jobIds());
        return ResponseEntity.noContent().build();
    }


}
