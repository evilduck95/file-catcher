package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class MusicService extends FileService {


    public MusicService(final FileDefaults fileDefaults) {
        super(fileDefaults, "audio");
    }

    @Override
    public void save(Resource media, String contentType) {
        if (correctContentType(contentType)) {
//            musicRepository.save(media);
        } else {
            throw new IncorrectFileFormatException("File is not Audio");
        }
    }
}
