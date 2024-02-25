package com.evilduck.filecatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Media {

    private String name;
    private String extension;
    private File file;
    private Year releaseYear;

}
