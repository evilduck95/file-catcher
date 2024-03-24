package com.evilduck.filecatcher.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
public class JobRunResult {

    @Id
    private String id;
    private boolean successful;
    private List<JobError> errors = new ArrayList<>();

    public void addError(final JobError jobError) {
        errors.add(jobError);
    }

}
