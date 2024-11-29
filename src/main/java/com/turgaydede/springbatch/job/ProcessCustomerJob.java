package com.turgaydede.springbatch.job;

import com.turgaydede.springbatch.listener.EmptyReaderCheckListener;
import com.turgaydede.springbatch.listener.StepLoggingListener;
import com.turgaydede.springbatch.entity.Customer;
import com.turgaydede.springbatch.entity.ProcessedCustomer;
import com.turgaydede.springbatch.processor.DatabaseProcessor;
import com.turgaydede.springbatch.repository.ProcessedCustomerRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ProcessCustomerJob {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final ProcessedCustomerRepository processedCustomerRepository;
    private final DatabaseProcessor databaseProcessor;

    @Bean
    @StepScope
    public JpaPagingItemReader<Customer> databaseReader(@Value("#{jobParameters['country']}") String country) {
        JpaPagingItemReader<Customer> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT c FROM Customer c WHERE c.country = :country and c.isProcessed = false");
        reader.setParameterValues(Map.of("country", country));
        reader.setPageSize(10);
        return reader;
    }

    @Bean
    public RepositoryItemWriter<ProcessedCustomer> processedDatabaseWriter() {
        return new RepositoryItemWriterBuilder<ProcessedCustomer>()
                .repository(processedCustomerRepository)
                .methodName("save")
                .build();
    }

    @Bean
    public Step processDatabaseStep(StepLoggingListener stepLoggingListener,
                                    EmptyReaderCheckListener emptyReaderCheckListener,
                                    JpaPagingItemReader<Customer> databaseReader) {

        return new StepBuilder("processDatabaseStep", jobRepository)
                .<Customer, ProcessedCustomer>chunk(10, transactionManager)
                .reader(databaseReader)
                .listener(emptyReaderCheckListener)
                .processor(databaseProcessor)
                .writer(processedDatabaseWriter())
                .listener(stepLoggingListener)
                .build();
    }

    @Bean
    public Job processCustomersJob(Step processDatabaseStep) {
        return new JobBuilder("processCustomersJob", jobRepository)
                .start(processDatabaseStep)
                .build();
    }
}
