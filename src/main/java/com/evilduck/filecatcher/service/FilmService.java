package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.configuration.FileDefaults;
import com.evilduck.filecatcher.exception.FileProcessingException;
import com.evilduck.filecatcher.exception.IncorrectFileFormatException;
import com.evilduck.filecatcher.model.Film;
import com.evilduck.filecatcher.model.Job;
import com.evilduck.filecatcher.respository.FilmRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Year;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class FilmService extends FileService {

    private static final Pattern RESOLUTION_PATTERN = Pattern.compile("(240|288|480|576|720|1080|1440|2160|4320)");
    private static final Pattern YEAR_PATTERN = Pattern.compile("([0-9]{4})");
    private static final Pattern FILM_NAME_PATTERN = Pattern.compile("(.*?)_([0-9]{4})");
    private final JobDirectoryManager jobDirectoryManager;
    private final FilmRepository filmRepository;
    private final JobQueueService jobQueueService;

    public FilmService(FilmRepository filmRepository,
                       FileDefaults fileDefaults,
                       JobDirectoryManager jobDirectoryManager,
                       JobQueueService jobQueueService) {
        super(fileDefaults, "video");
        this.filmRepository = filmRepository;
        this.jobDirectoryManager = jobDirectoryManager;
        this.jobQueueService = jobQueueService;
    }

    @Override
    public String save(final InputStream inputStream,
                       final String fileName,
                       final String contentType) throws IOException {
        if (correctContentType(contentType)) {
            return jobDirectoryManager.tempStoreStreamAsFile(fileName, inputStream);
        } else {
            throw new IncorrectFileFormatException(fileName, "File is not a ZIP Archive or Video");
        }
    }

    @Override
    public String saveOrAppend(InputStream inputStream, String fileName, String contentType) throws IOException {
        if (correctContentType(contentType)) {
            return jobDirectoryManager.appendStreamToFile(fileName, inputStream);
        } else {
            throw new IncorrectFileFormatException(fileName, "File is not a ZIP Archive or Video");
        }
    }

    @Override
    public void process(final List<String> jobIds) {
        for (final String id : jobIds) {
            final File filmsFolder = jobDirectoryManager.getJobDirectory(id);
            final Job job = new Job(id, () -> parseFilm(filmsFolder));
            jobQueueService.addJob(job);
        }
    }

    private void parseFilm(final File filmFolder) throws FileProcessingException {
        final File[] filmFiles = safeListDirectory(filmFolder);
        for (File film : filmFiles) {
            if(film.isFile()){
                log.info("Got film, filename: [{}]", film.getName());
                String filmFileNameCleansed = cleanseName(film.getName());
                log.info("Filename cleansed, new filename: [{}]", filmFileNameCleansed);
                final Film filmOut = new Film();
                filmOut.setFile(film);
                filmOut.setResolution(parseResolution(filmFileNameCleansed));
                filmOut.setReleaseYear(parseYear(filmFileNameCleansed, filmOut.getResolution()));
                filmOut.setExtension(parseExtension(film.getName()));
                filmOut.setName(parseFilmName(filmFileNameCleansed));
                log.info("Found following information for film: [{}]", filmOut);
                try {
                    filmRepository.save(filmOut);
                } catch (IOException e) {
                    log.error("Something went wrong writing the film: [{}], message: [{}]", film.getName(), e.getMessage());
                    log.error("IOException trace", e);
                    throw new FileProcessingException(film.getName(), e.getMessage());
                }
            }
        }
        cleanupFilmFolder(filmFolder, filmFiles);
    }

    private void cleanupFilmFolder(final File filmFolder, final File[] filmFiles) {
        for (File filmFile : filmFiles) {
            try {
                FileUtils.delete(filmFile);
            } catch (IOException e) {
                log.error("There was a problem cleaning up Film directory [{}] {}", filmFolder.getName(), e.getMessage());
            }
        }
    }

    private File[] safeListDirectory(final File directory) {
        final File[] list = directory.listFiles();
        if (list == null) throw new RuntimeException("Unable to list directory [" + directory.getPath() + ']');
        return list;
    }

    private int parseResolution(final String filename) {
        Matcher resolutionMatch = RESOLUTION_PATTERN.matcher(filename);
        if(resolutionMatch.find()) {
            log.info("Found resolution [{}] for file.", resolutionMatch.group(1));
            return resolutionMatch.groupCount() == 1 ? Integer.parseInt(resolutionMatch.group(1)) : 0;
        }
        return 0;
    }

    private Year parseYear(final String filename, int resolution){
        Matcher yearMatch = YEAR_PATTERN.matcher(filename);
        if(yearMatch.find()) {
            // Assume the first 4-digit number that is not the resolution would be the year of release
            for (int groupCounter = 0; groupCounter < yearMatch.groupCount(); groupCounter++) {
                int testValue = Integer.parseInt(yearMatch.group(groupCounter + 1));
                if (testValue != resolution) {
                    log.info("Found year [{}] for file.", testValue);
                    return Year.of(testValue);
                }
            }
        }
        return null;
    }

    private String parseFilmName(final String filename){
        Matcher filmNameMatch = FILM_NAME_PATTERN.matcher(filename);
        if(filmNameMatch.find()){
            log.info("Found name [{}] for file.", filmNameMatch.group(1));
            return filmNameMatch.group(1);
        }
        return "";
    }

}
