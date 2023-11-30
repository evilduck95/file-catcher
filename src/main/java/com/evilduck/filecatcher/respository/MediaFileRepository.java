package com.evilduck.filecatcher.respository;

import org.springframework.core.io.Resource;

import java.util.Optional;

public interface MediaFileRepository {

    Optional<String> save(Resource file);

}
