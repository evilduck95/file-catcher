package com.evilduck.filecatcher.controller;

import com.evilduck.filecatcher.dto.FileUploadResponse;
import com.evilduck.filecatcher.service.FileService;
import com.evilduck.filecatcher.service.FilmService;
import com.evilduck.filecatcher.service.MusicService;
import com.evilduck.filecatcher.service.ZipManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
public class MediaFileController {

    private final FilmService filmService;
    private final MusicService musicService;
    private final ZipManager zipManager;

    public MediaFileController(FilmService filmService,
                               MusicService musicService,
                               ZipManager zipManager) {
        this.filmService = filmService;
        this.musicService = musicService;
        this.zipManager = zipManager;
    }

    @PostMapping("/film")
    public ResponseEntity<FileUploadResponse> uploadFilm(@RequestParam("file") final MultipartFile file) {
        return handleFileWithService(file, filmService);
    }

    @PostMapping("/music")
    public ResponseEntity<FileUploadResponse> uploadMusic(@RequestParam("file") final MultipartFile file) {
        return handleFileWithService(file, musicService);
    }

    // TODO: THIS IS A TEST, IMPLEMENT THIS FOR OTHER UPLOADS.
    @PostMapping("/zip")
    public ResponseEntity<FileUploadResponse> uploadTest(@RequestParam("file") final MultipartFile file) throws IOException {
        zipManager.unzipAlbum(file.getResource());
        return ResponseEntity.of(Optional.empty());
    }


    private ResponseEntity<FileUploadResponse> handleFileWithService(final MultipartFile file, final FileService fileService) {
        final Optional<String> errorMessage = fileService.save(file.getResource(), file.getContentType());
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
