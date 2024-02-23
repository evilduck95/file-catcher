package com.evilduck.filecatcher.model;

import java.util.ArrayList;
import java.util.List;

public record TvShow(String name, List<Season> seasons) {

    public TvShow(String name) {
        this(name, new ArrayList<>());
    }

    public void addEpisode(final int seasonNumber,
                           final int episodeNumber,
                           final Episode episode) {
        if (seasons.size() <= seasonNumber) {
            seasons.add(new Season(seasonNumber, new ArrayList<>()));
        }
        final Season season = seasons.get(seasonNumber);
        season.addEpisode(episodeNumber, episode);
    }

}
