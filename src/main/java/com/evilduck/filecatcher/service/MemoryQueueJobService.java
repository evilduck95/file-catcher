package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class MemoryQueueJobService implements JobQueueService {

    private static final Queue<Job> JOB_QUEUE = new LinkedBlockingQueue<>();

    private final AtomicReference<Job> currentJob = new AtomicReference<>();

    @Override
    public void addJob(final Job job) {
        JOB_QUEUE.offer(job);
        log.info("Added job to queue [{}]", job.jobId());
    }

    @Scheduled(fixedRate = 10000L)
    public void checkAndRunJobs() {
        if (currentJob.get() == null) {
            log.info("No job Running, checking for new jobs...");
            final Job nextJob = JOB_QUEUE.poll();
            if (nextJob == null) return;
            currentJob.set(nextJob);
            nextJob.jobRunner().run();
        }
    }

}
