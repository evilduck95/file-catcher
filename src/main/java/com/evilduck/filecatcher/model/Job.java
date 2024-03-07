package com.evilduck.filecatcher.model;

public record Job(String jobId, Runnable jobRunner) {

}
