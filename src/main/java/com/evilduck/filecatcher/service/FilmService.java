package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.respository.FilmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class FilmService implements FileService {

    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    @Override
    public Optional<String> save(Resource film) {
       return filmRepository.save(film);
    }

}
