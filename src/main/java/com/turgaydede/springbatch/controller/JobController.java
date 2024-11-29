package com.turgaydede.springbatch.controller;

import lombok.RequiredArgsConstructor;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final JobLauncher jobLauncher;
    private final Scheduler scheduler;
    private final ApplicationContext applicationContext;

    @PostMapping("/startJob")
    public ResponseEntity<String> launchJob(@RequestParam("jobName") String jobName) {
        try {
            Job job = applicationContext.getBean(jobName, Job.class);

            JobParametersBuilder paramsBuilder = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis());

            JobParameters jobParameters = paramsBuilder.toJobParameters();

            JobExecution jobExecution = jobLauncher.run(job, jobParameters);

            return ResponseEntity.ok("Job executed: " + jobName + " with status: " + jobExecution.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Job execution failed: " + e.getMessage());
        }
    }


    @PostMapping("/triggerScheduledJob")
    public void startScheduledJobTrigger(@RequestParam String jobName) {
        try {
            JobKey jobKey = new JobKey(jobName, "batchJobs");
            if (scheduler.checkExists(jobKey)) {
                scheduler.triggerJob(jobKey);
                System.out.println("Job triggered: " + jobName);
            } else {
                System.out.println("Job does not exist: " + jobName);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

