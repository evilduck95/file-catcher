package com.evilduck.filecatcher.configuration;

import com.evilduck.filecatcher.respository.FileRepository;
import com.evilduck.filecatcher.respository.FilmRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoriesConfiguration {

    @Bean
    public FilmRepository filmRepository(@Value("${directories.films}") final String filmsDirectory) {
        return new FilmRepository(filmsDirectory);
    }

    @Bean
    public FileRepository musicRepository(@Value("${directories.music}") final String musicDirectory) {
        return new FileRepository(musicDirectory);
    }

    @Bean
    public FileRepository tvShowRepository(@Value("${directories.tv-shows}") final String tvShowsDirectory) {
        return new FileRepository(tvShowsDirectory);
    }

}
