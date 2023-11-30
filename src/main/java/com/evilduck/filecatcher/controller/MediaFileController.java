package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.service.FilmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
public class MediaFileController {

    private final FilmService filmService;

    public MediaFileController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/film")

    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") final MultipartFile file) {
        final Optional<String> errorMessage = filmService.save(file.getResource());
        final FileUploadResponse response = new FileUploadResponse();
        if (errorMessage.isPresent()) {
            response.setMessage(errorMessage.get());
            return ResponseEntity.badRequest().body(response);
        } else {
            response.setMessage("File Saved Successfully");
            return ResponseEntity.ok(response);
        }
    }

}
