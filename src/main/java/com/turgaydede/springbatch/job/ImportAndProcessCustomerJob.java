package com.turgaydede.springbatch.job;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ImportAndProcessCustomerJob {
    private final JobRepository jobRepository;

    @Bean
    public Job conditionalJob(Step csvToDatabaseStep, Step processDatabaseStep) {
        return new JobBuilder("conditionalJob", jobRepository)
                .start(csvToDatabaseStep)
                .next(((jobExecution, stepExecution) -> {
                    if (stepExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                        return new FlowExecutionStatus("NEXT");
                    } else {
                        return new FlowExecutionStatus("STOP");
                    }
                }))
                .on("NEXT").to(processDatabaseStep)
                .end()
                .build();
    }
}
