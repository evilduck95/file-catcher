package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.respository.FileRepository;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MusicService extends FileService {

    private final FileRepository musicRepository;

    public MusicService(final FileDefaults fileDefaults,
                        final FileRepository musicRepository) {
        super(fileDefaults, "audio");
        this.musicRepository = musicRepository;
    }

    @Override
    public void save(Resource media, String contentType) {
        if (correctContentType(contentType)) {
            musicRepository.save(media);
        } else {
            throw new IncorrectFileFormatException("File is not Audio");
        }
    }
}
