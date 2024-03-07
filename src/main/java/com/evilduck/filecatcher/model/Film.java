package com.evilduck.filecatcher.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Film extends Media {

    private int resolution;
    private Subtitles subtitles;

    @Override
    public String toString(){
        return String.format("Film[%s resolution[%s] subtitles[%s]]",
                super.toString(),
                resolution,
                subtitles.toString());
    }

}

