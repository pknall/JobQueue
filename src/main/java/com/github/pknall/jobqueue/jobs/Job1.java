package com.github.pknall.jobqueue.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Job1 extends Job {

    private static final Logger logger = LoggerFactory.getLogger(Job1.class);

    public Job1() {
        super("Job 1" + new Date().getTime());
        this.setRunnable(this::run);
    }

    @Override
    public String call() {
        try { Thread.sleep(15000); }
        catch (Exception ex) {
            System.out.println(ex);
        }
        logger.info("Call made to Log1");
        setJobStatus(JobStatus.DONE);
        return "Called Log1";
    }

    @Override
    public void run() {
        System.out.println(call());
    }
}
