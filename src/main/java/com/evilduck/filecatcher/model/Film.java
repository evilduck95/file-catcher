package com.evilduck.filecatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Film extends Media {

    private int resolution;
    private Subtitles subtitles;

    public Film() {

    }
}
