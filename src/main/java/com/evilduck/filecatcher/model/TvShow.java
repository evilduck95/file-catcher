package com.evilduck.filecatcher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TvShow extends Media {

    private int resolution;
    private int season;
    private List<File> episodes;

}
