package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.dto.JobFileError;
import com.evilduck.filecatcher.dto.JobStatusResponse;
import com.evilduck.filecatcher.exception.FileProcessingException;
import com.evilduck.filecatcher.model.Job;
import com.evilduck.filecatcher.model.JobError;
import com.evilduck.filecatcher.model.JobRunResult;
import com.evilduck.filecatcher.respository.JobRunResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class MemoryQueueJobService implements JobQueueService {

    private static final Integer MAX_JOB_COUNT = 3;
    private static final Queue<Job> JOB_QUEUE = new LinkedBlockingQueue<>();

    private final AtomicInteger jobRunningCount = new AtomicInteger(0);
    private final JobRunResultRepository jobRunResultRepository;

    public MemoryQueueJobService(JobRunResultRepository jobRunResultRepository) {
        this.jobRunResultRepository = jobRunResultRepository;
    }

    @Override
    public void addJob(final Job job) {
        final JobRunResult result = new JobRunResult();
        result.setId(job.jobId());
        final boolean offerSuccess = JOB_QUEUE.offer(job);
        if (offerSuccess) {
            result.setSuccessful(null);
        } else {
            result.setSuccessful(false);
            final JobError jobError = new JobError();
            jobError.setErrorMessage("Failed to add Job to Queue");
            result.addError(jobError);
        }
        jobRunResultRepository.save(result);
        log.info("Added job to queue [{}]", job.jobId());
    }

    @Override
    public JobStatusResponse checkJobResult(final String jobId) {
        final Optional<JobRunResult> jobRunResult = jobRunResultRepository.findById(jobId);
        if (jobRunResult.isPresent()) {
            final JobRunResult result = jobRunResult.get();
            if (result.getSuccessful() == null) {
                return new JobStatusResponse("Pending", "This Job hasn't yet started", null);
            } else if (result.getSuccessful()) {
                return new JobStatusResponse(
                        "Successful",
                        "This Job has completed successfully",
                        null
                );
            } else {
                return new JobStatusResponse(
                        "Unsuccessful",
                        "There were problems with completing this Job",
                        result.getErrors().stream().map(this::mapToJobFileError).toList()
                );
            }
        } else {
            return new JobStatusResponse("Unknown", "Unable to find Job, has /process been called?", null);
        }

    }

    private JobFileError mapToJobFileError(final JobError jobError) {
        return new JobFileError(jobError.getMediaName(), jobError.getErrorMessage());
    }

    @Scheduled(fixedRate = 10000L)
    public void checkAndRunJobs() {
        final int currentJobCount = jobRunningCount.get();
        log.info("[{}/{}] jobs running, checking for more jobs.", currentJobCount, MAX_JOB_COUNT);
        while (jobRunningCount.get() < MAX_JOB_COUNT) {
            final Job nextJob = JOB_QUEUE.poll();
            if (nextJob == null) return;
            final Thread thread = new Thread(() -> {
                final JobRunResult result = new JobRunResult();
                result.setId(nextJob.jobId());
                result.setSuccessful(true);
                try {
                    nextJob.jobRunner().run();
                } catch (FileProcessingException e) {
                    result.setSuccessful(false);
                    final JobError error = new JobError();
                    error.setMediaName(e.getMediaName());
                    error.setErrorMessage(e.getErrorMessage());
                    result.addError(error);
                } catch (Exception e) {
                    result.setSuccessful(false);
                    final JobError error = new JobError();
                    error.setErrorMessage(e.getMessage());
                    result.addError(error);
                } finally {
                    jobRunResultRepository.save(result);
                    jobRunningCount.decrementAndGet();
                }
            });
            jobRunningCount.incrementAndGet();
            log.info("Running job [{}] on thread [{}]", nextJob.jobId(), thread.getName());
            thread.start();
        }
    }

}
