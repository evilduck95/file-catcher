package com.evilduck.filecatcher.configuration;

import com.evilduck.filecatcher.respository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoriesConfiguration {

    @Bean
    public FileRepository filmRepository(@Value("${directories.films}") final String filmsDirectory) {
        return new FileRepository(filmsDirectory);
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
