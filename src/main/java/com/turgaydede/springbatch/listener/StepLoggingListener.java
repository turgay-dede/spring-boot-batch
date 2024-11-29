package com.turgaydede.springbatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

@Component
public class StepLoggingListener extends StepExecutionListenerSupport {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Starting Step: " + stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("Finished Step: " + stepExecution.getStepName());
        System.out.println("Read Count: " + stepExecution.getReadCount());
        System.out.println("Write Count: " + stepExecution.getWriteCount());
        return null;
    }
}