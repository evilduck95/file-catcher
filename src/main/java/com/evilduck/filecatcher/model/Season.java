package com.evilduck.filecatcher.model;

import java.util.Arrays;

public record Season(int seasonNumber, Episode[] episodes) {

    public Season(int seasonNumber, int numberOfEpisodes) {
        this(seasonNumber, new Episode[numberOfEpisodes]);
    }

    @Override
    public String toString(){
        return String.format("Season[seasonNumber[%s] Episodes[%s]]",
                seasonNumber,
                Arrays.toString(episodes));
    }

    public void addEpisode(final int episodeNumber, final Episode episode) {
        episodes[episodeNumber] = episode;
    }

}
