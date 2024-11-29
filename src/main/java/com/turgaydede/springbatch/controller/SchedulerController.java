package com.turgaydede.springbatch.controller;

import com.turgaydede.springbatch.scheduler.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class SchedulerController {

    @Autowired
    private JobScheduler quartzJobService;

    @PostMapping
    public String scheduleJob( @RequestBody Map<String, Object> jobDataMap) {
        quartzJobService.scheduleJob(jobDataMap);
        return "Job scheduled successfully!";
    }

    @PostMapping("/update")
    public String updateJobSchedule(@RequestBody Map<String, Object> request) {
        try {
            String jobName = (String) request.get("jobName");
            String groupName = (String) request.get("groupName");
            String newCronExpression = (String) request.get("newCronExpression");
            Map<String, Object> jobDataMap = (Map<String, Object>) request.get("jobDataMap");

            quartzJobService.updateJobSchedule(jobName, groupName, newCronExpression, jobDataMap);
            return "Job schedule updated successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating job schedule: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteJob(@RequestBody Map<String, Object> request) {
        try {
            String jobName = (String) request.get("jobName");
            String groupName = (String) request.get("groupName");

            quartzJobService.deleteJob(jobName, groupName);
            return "Job deleted successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting job: " + e.getMessage();
        }
    }
}
