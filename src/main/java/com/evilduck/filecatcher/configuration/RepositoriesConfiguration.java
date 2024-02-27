package com.evilduck.filecatcher.configuration;

import com.evilduck.filecatcher.respository.FilmRepository;
import com.evilduck.filecatcher.respository.TvShowRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoriesConfiguration {

    final FileDefaults fileDefaults;

    public RepositoriesConfiguration(final FileDefaults fileDefaults) {
        this.fileDefaults = fileDefaults;
    }

    @Bean
    public FilmRepository filmRepository(@Value("${directories.films}") final String filmsDirectory) {
        return new FilmRepository(fileDefaults, filmsDirectory);
    }

    @Bean
    public TvShowRepository tvShowRepository(@Value("${directories.tv-shows}") final String tvShowsDirectory) {
        return new TvShowRepository(fileDefaults, tvShowsDirectory);
    }

}
