package com.github.pknall.jobqueue;

import com.github.pknall.jobqueue.jobs.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobSchedulerTest {

    @Test
    void jobSchedulerNullArgumentTest() {
        JobScheduler jobScheduler = new JobScheduler();
        Throwable throwable = assertThrows(NullPointerException.class, () -> jobScheduler.addJobsToList(null));
        assertEquals("Null jobList sent to JobScheduler.addJobsToList()", throwable.getMessage());
    }

    @Test
    void jobSchedulerCallableFutureTest() {
        JobScheduler jobScheduler = new JobScheduler();
        ArrayList<Job> jobs = new ArrayList<>();
        jobs.add(new Job1());
        jobs.add(new Job2());
        jobScheduler.addJobsToList(jobs);

        try {
            Thread.sleep(2000);
        }
        catch (Exception ex) {

        }

        for (Job job : jobs) {
            try {
                System.out.println(job.getFutureResults().get());
            }
            catch (Exception ex) {

            }
        }

        jobScheduler.shutdown();
    }
}