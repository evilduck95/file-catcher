package com.evilduck.filecatcher.respository;

import com.evilduck.filecatcher.model.JobRunResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRunResultRepository extends MongoRepository<JobRunResult, String> {
}
