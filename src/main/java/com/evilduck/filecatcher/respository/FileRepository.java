package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.configuration.FileDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class FileRepository {

    FileDefaults fileDefaults;
    final String directory;

    public FileRepository(final FileDefaults fileDefaults,
                          final String directory) {
        this.fileDefaults = fileDefaults;
        this.directory = directory;
    }

}
