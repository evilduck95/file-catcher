package com.evilduck.filecatcher.service;

import org.springframework.core.io.Resource;

import java.util.Optional;


public interface FileService {

    Optional<String> save(Resource media);

}
