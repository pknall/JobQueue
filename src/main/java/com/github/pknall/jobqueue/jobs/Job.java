package com.github.pknall.jobqueue.jobs;

import lombok.Data;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Data
public abstract class Job implements Callable<String> {

    private Future<String> futureResults = null;
    private Callable<String> callableJob = null;
    private Runnable jobToRun = null;

    @Override
    public String call() throws Exception {
        return null;
    }
}
