package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.service.FilmService;
import com.evilduck.filecatcher.service.TvShowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class MediaFileController {

    private final FilmService filmService;
    private final TvShowService tvShowService;

    public MediaFileController(final FilmService filmService,
                               final TvShowService tvShowService) {
        this.filmService = filmService;
        this.tvShowService = tvShowService;
    }

    @PostMapping("/film")
    public ResponseEntity<FileUploadResponse> uploadFilm(@RequestParam("file") final MultipartFile file) throws IOException {
        filmService.save(file.getResource(), file.getContentType());
        final FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setMessage("Success");
        return ResponseEntity.ok(fileUploadResponse);
    }

    @PostMapping("/tv-show")
    public ResponseEntity<FileUploadResponse> uploadTvShow(@RequestParam("file") final MultipartFile file) throws IOException {
        tvShowService.save(file.getResource(), file.getContentType());
        final FileUploadResponse fileUploadResponse = new FileUploadResponse();
        fileUploadResponse.setMessage("Success");
        return ResponseEntity.ok(fileUploadResponse);
    }

}
