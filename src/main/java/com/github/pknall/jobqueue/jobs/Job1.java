package com.github.pknall.jobqueue.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Job1 extends Job {

    private static final Logger logger = LoggerFactory.getLogger(Job1.class);

    @Override
    public String call() {
        logger.info("Call made to Log1");
        return "Called Log1";
    }
}
