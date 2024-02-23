package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.model.Film;
import com.evilduck.filecatcher.model.TvShow;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

public interface MediaFileRepository {

    String save(Resource file);

    String save(File file, String name) throws IOException;
}
