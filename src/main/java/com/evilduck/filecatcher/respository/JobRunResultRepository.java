package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.model.JobRunResult;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface JobRunResultRepository extends MongoRepository<JobRunResult, String> {

    List<JobRunResult> findAllBySuccessful(final boolean successful);

}
