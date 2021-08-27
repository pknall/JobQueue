package com.github.pknall.jobqueue.jobs;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Data
public class Job implements Callable<String>, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Job.class);

    private Future<String> futureResults = null;
    private Callable<String> callableJob = null;
    private JobStatus jobStatus = JobStatus.UNCONFIGURED;
    private String name;
    private Thread threadRunningJob = null;                 // Keep tabs on thread running job.  Assigned by JobExecutor.runJob().
    private Runnable runningJob = null;                     // Assigned to wrapper: JobExecutor.runJob().  Set and called by JobExecutor.launcher().
    private Runnable runnable = null;                       // Assigned by Job Subclass Constructor.  Called by JobExecutor.runJob().  References Runnable task in Job Subclass.
    //private Runnable jobToRun = null;

    public Job(){
        this("Anonymous " + new Date().toString());
        this.setJobStatus(JobStatus.QUEUED);
    }

    public Job(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        return;
    }

    @Override
    public String call() throws Exception {
        return null;
    }

    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(" : ");
        sb.append(getJobStatus());
        if (getJobStatus() == JobStatus.RUNNING && getThreadRunningJob() != null) {
            sb.append(" on thread ");
            sb.append(getThreadRunningJob().getId());
        }
        return sb.toString();
    }
}
