package com.github.pknall.jobqueue;

import lombok.Data;

@Data
public class JobSchedulerConfig {
    private int maxThreads = 10;
    private int launcherThrottleTime = 100;
}
