package com.github.pknall.jobqueue;

import com.github.pknall.jobqueue.jobs.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobSchedulerTest {

    @Test
    void jobSchedulerNullArgumentTest() {
        JobExecutor jobScheduler = new JobExecutor();
        Throwable throwable = assertThrows(NullPointerException.class, () -> jobScheduler.submitJobsForExecution(null));
        assertEquals("Null jobList sent to JobScheduler.addJobsToList()", throwable.getMessage());
    }

    @Test
    void jobSchedulerCallableFutureTest() {
        JobExecutor jobScheduler = new JobExecutor();
        ArrayList<Job> jobs = new ArrayList<>();
        jobs.add(new Job1());
        jobs.add(new Job2());
        jobScheduler.submitJobsForExecution(jobs);               // Send to Scheduler to Run

        for (int i = 0; i < 60; i++) {
            if (i < 20) jobScheduler.submitJobForExecution(new Job1());
            try { Thread.sleep(1000); }
            catch (Exception ex) {
                System.out.println(ex);
            }
            System.out.println("- - - - -");
            int count = 1;
            for(Job job : jobScheduler.jobStatus()) {
                System.out.println(count++ + " : " + job.getStatus());
            }
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