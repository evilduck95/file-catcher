package com.evilduck.filecatcher.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class JobError {

    private String mediaName;
    private String errorMessage;

}
