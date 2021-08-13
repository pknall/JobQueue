package com.github.pknall.jobqueue.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Job2 implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Job2.class);
    public void run() {
        logger.info("Executing...");
        System.out.println("MORE WORK!");
    }
}

