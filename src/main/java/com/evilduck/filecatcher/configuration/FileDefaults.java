package com.evilduck.filecatcher.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@AllArgsConstructor
@ConfigurationProperties("file-defaults")
public class FileDefaults {

    private char delimiter;
    private String cleanseRegex;


}
