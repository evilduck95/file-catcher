package com.evilduck.filecatcher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Episode extends Media {

    private final int episodeNumber;
    private Subtitles subtitles;

    @Override
    public String toString(){
        return String.format("Episode[%s episodeNumber[%s] subtitles[%s]]",
                super.toString(),
                episodeNumber,
                subtitles.toString());
    }

}
