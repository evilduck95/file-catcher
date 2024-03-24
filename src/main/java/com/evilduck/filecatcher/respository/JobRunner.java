package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.exception.FileProcessingException;

@FunctionalInterface
public interface JobRunner {

    void run() throws FileProcessingException;

}
