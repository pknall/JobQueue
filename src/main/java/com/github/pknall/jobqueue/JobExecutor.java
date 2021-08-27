package com.github.pknall.jobqueue;

import com.github.pknall.jobqueue.jobs.Job;
import com.github.pknall.jobqueue.jobs.JobStatus;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

public class JobExecutor {

    private static final Logger logger = LoggerFactory.getLogger(JobExecutor.class);

    private final ThreadPoolExecutor threadPoolExecutor;
    private volatile boolean done = false;
    private final Map<String, Job> indexedJobsByName;       // Prevent Duplicates / Provides Status
    private final ArrayList<Job> jobsToSubmitForExecution;
    private final ArrayList<Job> jobsToGetInTheFuture;
    private final JobExecutorConfig config;
    private final AtomicBoolean launcherNotifier;

    public JobExecutor() {
        this(new JobExecutorConfig());
    }

    public JobExecutor(JobExecutorConfig config) {
        this.config = config;
        this.launcherNotifier = new AtomicBoolean(false);
        this.jobsToSubmitForExecution = new ArrayList<>();
        this.jobsToGetInTheFuture = new ArrayList<>();
        this.indexedJobsByName = new ConcurrentHashMap<>();
        this.threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(config.getMaxThreads());
        Thread launcherThread = new Thread(this::launcher, "Job Scheduler");
        if (launcherThread.isDaemon()) {
            launcherThread.setDaemon(false);
        }
        launcherThread.start();
    }

    @SneakyThrows
    private void launcher() {
        while (!done) {
            synchronized (launcherNotifier) {
                logger.info("Loop...");
                while (jobsToSubmitForExecution.size() > 0) {
                    Job jobToSubmitForExecution = jobsToSubmitForExecution.remove(0);
                    jobToSubmitForExecution.setJobStatus(JobStatus.QUEUED);
                    jobsToGetInTheFuture.add(jobToSubmitForExecution);
                    indexedJobsByName.put(jobToSubmitForExecution.getName(), jobToSubmitForExecution);
                    //jobToSubmitForExecution.setFutureResults(threadPoolExecutor.submit(jobToSubmitForExecution));
                    jobToSubmitForExecution.setRunningJob(() -> runJob(jobToSubmitForExecution));
                    threadPoolExecutor.execute(jobToSubmitForExecution.getRunningJob());

                    // jobToSubmitForExecution.setFutureResults(threadPoolExecutor.submit(jobToSubmitForExecution.getCallableJob()));
                }
                Thread.sleep(config.getLauncherThrottleTime());
                launcherNotifier.wait();
            }
        }
    }

    /**
     * Provides reference to a collection of Job objects
     * @return
     */
    public Collection<Job> jobStatus() {
        return indexedJobsByName.values();
    }

    /**
     * @param name Name of Job currently in Queue or running
     * @return Job Reference to the Job being run...maybe (Optional&lt;T&gt;)
     */
    public Optional<Job> findJob(String name) {
        return Optional.ofNullable(indexedJobsByName.get(name));
    }



    /**
     * Queue an execution of this process
     * @param jobToSubmitForExecution
     */
    public void submitJobForExecution(Job jobToSubmitForExecution) {
        Objects.requireNonNull(jobToSubmitForExecution, "Null Job sent to JobScheduler.addJobForExecution()");
        jobToSubmitForExecution.setJobStatus(JobStatus.READY);
        synchronized (indexedJobsByName) {
            //Job lastJob = findJob(jobToSubmitForExecution.getName()).orElse(null);
           //if (lastJob != null && lastJob.getJobStatus() != JobStatus.DONE) {
            //    throw new IllegalArgumentException("A job is already scheduled with the name:");
            //}

            indexedJobsByName.put(jobToSubmitForExecution.getName(), jobToSubmitForExecution);
            jobsToSubmitForExecution.add(jobToSubmitForExecution);
        }
        synchronized (launcherNotifier) {
            launcherNotifier.notify();
        }
    }

    public void submitJobsForExecution(ArrayList<Job> jobList) {
        Objects.requireNonNull(jobList, "Null jobList sent to JobScheduler.addJobsToList()");
        for (Job jobToSubmitForExecution : jobList) {
            submitJobForExecution(jobToSubmitForExecution);
        }
    }

    public void shutdown() {
        logger.info("Shutting down.");
        threadPoolExecutor.shutdown();
        done = true;
    }

    /**
     * Wraps jobToRun so that additional maintenance can be performed when the Executor calls the job:
     *      Set JobStatus to RUNNING
     *      Create a reference to the currentThread
     *      Catch any exceptions during execution
     *      When complete, remove Job from indexedJobsByName
     * @param jobToRun
     */
    private void runJob(Job jobToRun) {
        jobToRun.setJobStatus(JobStatus.RUNNING);
        jobToRun.setThreadRunningJob(Thread.currentThread());

        try {
            jobToRun.getRunnable().run();
        }
        catch(Throwable t) {
            logger.error("Error during job '{}' execution", jobToRun.getName(), t);
        }

        jobToRun.setThreadRunningJob(null);

        synchronized (indexedJobsByName) {
            indexedJobsByName.remove(jobToRun.getName());
        }
    }
}
