package com.evilduck.filecatcher.model;

public record Season(int seasonNumber, Episode[] episodes) {

    public Season(int seasonNumber, int numberOfEpisodes) {
        this(seasonNumber, new Episode[numberOfEpisodes]);
    }

    public void addEpisode(final int episodeNumber, final Episode episode) {
        episodes[episodeNumber] = episode;
    }

}
