package com.evilduck.filecatcher.model;

import java.util.List;

public record Season(int seasonNumber, List<Episode> episodes) {

    public void addEpisode(final int episodeNumber, final Episode episode) {
        episodes.add(episodeNumber, episode);
    }

}
