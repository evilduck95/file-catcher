package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.respository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FilmService extends FileService {

    private final FileRepository filmRepository;

    public FilmService(FileRepository filmRepository) {
        super("video");
        this.filmRepository = filmRepository;
    }

    @Override
    public void save(final Resource film, final String contentType) {
        if(correctContentType(contentType)) {
            filmRepository.save(film);
        } else {
            throw new IncorrectFileFormatException("File is not a Video");
        }
    }

}
