package com.evilduck.filecatcher.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Film extends Media {

    private int resolution;

}
