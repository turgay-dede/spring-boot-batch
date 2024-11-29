package com.turgaydede.springbatch.scheduler;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class JobScheduler {

    @Autowired
    private Scheduler scheduler;

    public String scheduleJob(Map<String, Object> jobDataMap) {
        try {
            String jobName = (String) jobDataMap.get("jobName");
            String jobGroup = (String) jobDataMap.get("jobGroup");
            String triggerGroup = (String) jobDataMap.get("triggerGroup");
            String triggerName = (String) jobDataMap.get("triggerName");
            String cronExpression = (String) jobDataMap.get("cronExpression");

            JobDetail jobDetail = JobBuilder.newJob(ScheduledBatchJobRunner.class)
                    .withIdentity(jobName, jobGroup)
                    .build();

            jobDetail.getJobDataMap().putAll(jobDataMap);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(triggerName, triggerGroup)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)) // Cron ifadesiyle zamanlama
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            return "Job successfully scheduled with cron expression: " + cronExpression;
        } catch (SchedulerException e) {
            e.printStackTrace();
            return "Error scheduling job: " + e.getMessage();
        }
    }

    public void updateJobSchedule(String jobName, String groupName, String newCronExpression, Map<String, Object> newJobDataMap) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(jobName + "Trigger", groupName + "Triggers");
        JobKey jobKey = new JobKey(jobName, groupName);

        if (scheduler.checkExists(triggerKey) && scheduler.checkExists(jobKey)) {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.putAll(newJobDataMap);

            JobDetail updatedJobDetail = JobBuilder.newJob(jobDetail.getJobClass())
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .storeDurably()
                    .build();

            Trigger newTrigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder.cronSchedule(newCronExpression))
                    .build();

            scheduler.addJob(updatedJobDetail, true);

            scheduler.rescheduleJob(triggerKey, newTrigger);

            System.out.println("Job rescheduled with updated JobDataMap: " + jobName);
        } else {
            System.out.println("Trigger or Job not found for job: " + jobName);
        }
    }


    public void deleteJob(String jobName, String groupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, groupName);

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            System.out.println("Job deleted: " + jobName);
        } else {
            System.out.println("Job not found: " + jobName);
        }
    }

}
