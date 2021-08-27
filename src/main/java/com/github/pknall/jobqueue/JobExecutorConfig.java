package com.github.pknall.jobqueue;

import lombok.Data;

@Data
public class JobExecutorConfig {
    private int maxThreads = 10;
    private int launcherThrottleTime = 1000;
}
