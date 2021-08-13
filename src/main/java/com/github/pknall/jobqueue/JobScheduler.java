package com.github.pknall.jobqueue;

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
    private final ArrayList<Runnable> jobs;
    private final JobSchedulerConfig config;

    public JobScheduler() {
        this(new JobSchedulerConfig());
    }

    public JobScheduler(JobSchedulerConfig config) {
        this.config = config;
        jobs = new ArrayList<>();
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
            for (Runnable job : jobs) {
                threadPool.execute(job);
            }
            jobs.clear();
            Thread.sleep(config.getLauncherThrottleTime());
        }
    }

    public void addJobsToList(ArrayList<Runnable> jobList) {
        Objects.requireNonNull(jobList, "Null jobList sent to JobScheduler.addJobsToList()");
        jobs.addAll(jobList);
    }

    public void shutdown() {
        logger.info("Shutting down.");
        done = true;
    }
}
