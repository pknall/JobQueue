package com.github.pknall.jobqueue.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Job2 extends Job {

    private static final Logger logger = LoggerFactory.getLogger(Job1.class);

    public Job2() {
        super("Job 2" + new Date().getTime());
        this.setRunnable(this::run);
    }

    @Override
    public String call() {
        try { Thread.sleep(10000); }
        catch (Exception ex) {
            System.out.println(ex);
        }
        logger.info("Call made to Log2");
        setJobStatus(JobStatus.DONE);
        return "Called Log2";
    }

    @Override
    public void run() {
        System.out.println(call());
    }
}

