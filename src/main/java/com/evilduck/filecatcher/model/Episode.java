package com.evilduck.filecatcher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Episode extends Media {

    private final int episodeNumber;

}
