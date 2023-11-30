package com.evilduck.filecatcher.model;

import lombok.Data;

import java.time.Year;

@Data
public abstract class Media {

    private String name;
    private String extension;
    private byte[] fileBytes;
    private Year releaseYear;

}
