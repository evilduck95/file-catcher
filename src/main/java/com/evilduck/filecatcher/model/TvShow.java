package com.evilduck.filecatcher.model;

import java.util.Arrays;

public record TvShow(String name, Season[] seasons) {

    public TvShow(String name, int numberOfSeasons) {
        this(name, new Season[numberOfSeasons]);
    }

    @Override
    public String toString(){
        return String.format("TvShow[name[%s] seasons[%s]]",
                name,
                Arrays.toString(seasons));
    }

    public void addSeason(final Season season,
                          final int seasonNumber) {
        seasons[seasonNumber] = season;
    }


    public Season getSeason(final int seasonNumber) {
        return seasonNumber < seasons.length ? seasons[seasonNumber] : null;
    }

}
