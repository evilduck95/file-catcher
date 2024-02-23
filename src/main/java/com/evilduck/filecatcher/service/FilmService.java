package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.model.Film;
import com.evilduck.filecatcher.respository.FilmRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FilmService extends FileService {

    private static final Pattern RESOLUTION_PATTERN = Pattern.compile("(240|288|480|576|720|1080|1440|2160|4320)");
    private static final Pattern YEAR_PATTERN = Pattern.compile("([0-9]{4})");
    private static final Pattern FILM_NAME_PATTERN = Pattern.compile("(.*)([0-9]{4})");
    private final FilmRepository filmRepository;

    public FilmService(FilmRepository filmRepository) {
        super("video");
        this.filmRepository = filmRepository;
    }

    private void parseFilm(final File filmFolder){
        File[] filmFiles = safeListDirectory(filmFolder);
        for(File film : filmFiles){
            if(film.isFile()){
                String filmFileNameCleansed = cleanseName(film.getName());
                final Film filmOut = new Film();
                filmOut.setFile(film);
                filmOut.setResolution(parseResolution(filmFileNameCleansed));
                filmOut.setReleaseYear(parseYear(filmFileNameCleansed, filmOut.getResolution()));
                filmOut.setExtension(parseExtension(filmFileNameCleansed));
                filmOut.setName(parseFilmName(filmFileNameCleansed));
                filmRepository.save(filmOut);
            }
        }
    }

    private File[] safeListDirectory(final File directory) {
        final File[] list = directory.listFiles();
        if (list == null) throw new RuntimeException("Unable to list directory [" + directory.getPath() + ']');
        return list;
    }

    private int parseResolution(final String filename){
        Matcher resolutionMatch = RESOLUTION_PATTERN.matcher(filename);
        if(resolutionMatch.find()) {
            return resolutionMatch.groupCount() == 1 ? Integer.valueOf(resolutionMatch.group(1)) : 0;
        }
        return 0;
    }

    private Year parseYear(final String filename, int resolution){
        Matcher yearMatch = YEAR_PATTERN.matcher(filename);
        if(yearMatch.find()) {
            // Assume the first 4 digit number that is not the resolution would be the year of release
            for (int groupCounter = 0; groupCounter < yearMatch.groupCount(); groupCounter++) {
                int testValue = Integer.valueOf(yearMatch.group(groupCounter + 1));
                if (testValue != resolution) {
                    return Year.of(testValue);
                }
            }
        }
        return Year.of(Year.MIN_VALUE);
    }

    private String parseFilmName(final String filename){
        // TODO: Add in parsing of film name, likely that the film name is going to be before the year or resolution
        Matcher filmNameMatch = FILM_NAME_PATTERN.matcher(filename);
        if(filmNameMatch.find()){
            return filmNameMatch.group(0);
        }
        return "";
    }

    @Override
    public void save(final Resource film, final String contentType) {
        if(correctContentType(contentType)) {
            filmRepository.save(film);
        } else {
            throw new IncorrectFileFormatException("File is not a Video");
        }
    }

}
