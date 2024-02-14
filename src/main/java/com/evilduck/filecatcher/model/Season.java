package com.evilduck.filecatcher.model;

import lombok.Data;

import java.util.List;

public record Season(int seasonNumber, List<Episode> episodes) {

}
