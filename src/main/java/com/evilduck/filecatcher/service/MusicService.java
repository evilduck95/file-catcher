package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.respository.FileRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MusicService extends FileService {

    private final FileRepository musicRepository;

    public MusicService(final FileRepository musicRepository) {
        super("audio");
        this.musicRepository = musicRepository;
    }

    @Override
    public Optional<String> save(Resource media, String contentType) {
        if (correctContentType(contentType)) {
            return musicRepository.save(media);
        } else {
            return Optional.of("File is not Audio");
        }
    }
}
