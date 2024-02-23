package com.evilduck.filecatcher.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("file-defaults")
public class FileDefaults {

    private char delimiter;
    private String cleanseRegex;
    private String extensionRegex;

}
