package com.turgaydede.springbatch.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ScheduledBatchJobRunner implements Job {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String jobName = context.getMergedJobDataMap().getString("jobName");

            org.springframework.batch.core.Job batchJob = applicationContext.getBean(jobName, org.springframework.batch.core.Job.class);

            JobParametersBuilder paramsBuilder = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis());

            context.getMergedJobDataMap().forEach((key, value) -> {
                if (!"jobName".equals(key)) {
                    paramsBuilder.addString(key, String.valueOf(value));
                }
            });

            JobParameters params = paramsBuilder.toJobParameters();

            JobExecution execution = jobLauncher.run(batchJob, params);
            System.out.println("Job Name: " + jobName + ", Status: " + execution.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new JobExecutionException("Job execution failed: " + e.getMessage(), e);
        }
    }

}
