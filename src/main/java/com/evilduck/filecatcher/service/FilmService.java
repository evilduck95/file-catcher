package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.respository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FilmService extends FileService {

    private final FileRepository filmRepository;

    public FilmService(FileRepository filmRepository) {
        super("video");
        this.filmRepository = filmRepository;
    }

    @Override
    public Optional<String> save(Resource film, final String contentType) {
        if(correctContentType(contentType)) {
            return filmRepository.save(film);
        } else {
            return Optional.of("File is not Video");
        }
    }

}
