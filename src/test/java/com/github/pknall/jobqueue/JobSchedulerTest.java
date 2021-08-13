package com.github.pknall.jobqueue;

import com.github.pknall.jobqueue.jobs.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobSchedulerTest {

    @Test
    void jobSchedulerTest() {
        JobScheduler jobScheduler = new JobScheduler();
        ArrayList<Runnable> jobs = new ArrayList<>();
        jobs.add(new Job1());
        jobs.add(new Job2());
        jobScheduler.addJobsToList(jobs);

        try {
            Thread.sleep(20000);
        }
        catch(Exception ex) {

        }

        jobScheduler.shutdown();
    }

    @Test
    void jobSchedulerNullArgumentTest() {
        JobScheduler jobScheduler = new JobScheduler();
        Throwable throwable = assertThrows(NullPointerException.class, () -> jobScheduler.addJobsToList(null));
        assertEquals("Null jobList sent to JobScheduler.addJobsToList()", throwable.getMessage());
    }
}