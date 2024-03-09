package com.evilduck.filecatcher.service;

import com.evilduck.filecatcher.model.Job;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class MemoryQueueJobService implements JobQueueService {

    private static final Integer MAX_JOB_COUNT = 3;
    private static final Queue<Job> JOB_QUEUE = new LinkedBlockingQueue<>();

    private final AtomicInteger jobRunningCount = new AtomicInteger(0);

    @Override
    public void addJob(final Job job) {
        JOB_QUEUE.offer(job);
        log.info("Added job to queue [{}]", job.jobId());
    }

    @Scheduled(fixedRate = 10000L)
    public void checkAndRunJobs() {
        final int currentJobCount = jobRunningCount.get();
        log.info("[{}/{}] jobs running, checking for more jobs.", currentJobCount, MAX_JOB_COUNT);
        while (jobRunningCount.get() < MAX_JOB_COUNT) {
            final Job nextJob = JOB_QUEUE.poll();
            if (nextJob == null) return;
            final Thread thread = new Thread(() -> {
                nextJob.jobRunner().run();
                jobRunningCount.decrementAndGet();
            });
            jobRunningCount.incrementAndGet();
            log.info("Running job [{}] on thread [{}]", nextJob.jobId(), thread.getName());
            thread.start();
        }
    }

}
