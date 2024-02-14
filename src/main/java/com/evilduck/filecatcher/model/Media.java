package com.evilduck.filecatcher.model;

import lombok.Data;

import java.io.File;
import java.time.Year;

@Data
public abstract class Media {

    private String name;
    private String extension;
    private File file;
    private Year releaseYear;

}
