package com.github.pknall.jobqueue.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Job1 implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Job1.class);
    public void run() {
        logger.info("Executing...");
        System.out.println("WORK! WORK!");
    }
}
