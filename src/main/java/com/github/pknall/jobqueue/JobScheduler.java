package com.github.pknall.jobqueue;

import com.github.pknall.jobqueue.jobs.Job;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

public class JobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final ThreadPoolExecutor threadPool;
    private volatile boolean done = false;
    private final ArrayList<Job> jobsToSubmitForExecution;
    private final ArrayList<Job> jobsToGetInTheFuture;
    private final JobSchedulerConfig config;

    public JobScheduler() {
        this(new JobSchedulerConfig());
    }

    public JobScheduler(JobSchedulerConfig config) {
        this.config = config;
        jobsToSubmitForExecution = new ArrayList<>();
        jobsToGetInTheFuture = new ArrayList<>();
        threadPool = new ScheduledThreadPoolExecutor(this.config.getMaxThreads());
        Thread launcherThread = new Thread(this::launcher, "Job Scheduler");
        if (launcherThread.isDaemon()) {
            launcherThread.setDaemon(false);
        }
        launcherThread.start();
    }

    @SneakyThrows
    private void launcher() {
        while (!done) {
            logger.info("Loop...");
            while (jobsToSubmitForExecution.size() > 0) {
                Job jobToSubmitForExecution = jobsToSubmitForExecution.remove(0);
                jobsToGetInTheFuture.add(jobToSubmitForExecution);
                jobToSubmitForExecution.setFutureResults(threadPool.submit(jobToSubmitForExecution));
            }
            Thread.sleep(config.getLauncherThrottleTime());
            // TODO: 8/14/2021 BUSY / WAITING?
        }
    }

    private void submitJob(Job jobToSubmit) {
        //jobToSubmit.setCallableJob(jobToSubmit.call());
    }

    public void addJobsToList(ArrayList<Job> jobList) {
        Objects.requireNonNull(jobList, "Null jobList sent to JobScheduler.addJobsToList()");
        jobsToSubmitForExecution.addAll(jobList);
    }

    public void shutdown() {
        logger.info("Shutting down.");
        threadPool.shutdown();
        done = true;
    }
}
