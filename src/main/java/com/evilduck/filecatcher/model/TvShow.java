package com.evilduck.filecatcher.model;

import lombok.Data;

import java.util.List;

public record TvShow(String name, List<Season> seasons) {

}
