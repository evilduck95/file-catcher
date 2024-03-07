package com.evilduck.filecatcher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;

@Data
@EqualsAndHashCode()
public class Subtitles {
    private File file;

    @Override
    public String toString(){
        return String.format("Subtitles[file[%s]]",
                file.getPath());
    }
}
