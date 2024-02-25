package com.evilduck.filecatcher.model;

public record TvShow(String name, Season[] seasons) {

    public TvShow(String name, int numberOfSeasons) {
        this(name, new Season[numberOfSeasons]);
    }

    public void addSeason(final Season season,
                          final int seasonNumber) {
        seasons[seasonNumber] = season;
    }


    public Season getSeason(final int seasonNumber) {
        return seasonNumber < seasons.length ? seasons[seasonNumber] : null;
    }

}
