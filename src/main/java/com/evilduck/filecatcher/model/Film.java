package com.evilduck.filecatcher.model;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends Media {

    private int resolution;
    private Subtitles subtitles;

}
