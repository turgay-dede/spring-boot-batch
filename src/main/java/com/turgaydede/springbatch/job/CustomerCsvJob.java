package com.turgaydede.springbatch.job;

import com.turgaydede.springbatch.listener.StepLoggingListener;
import com.turgaydede.springbatch.entity.Customer;
import com.turgaydede.springbatch.processor.CustomerProcessor;
import com.turgaydede.springbatch.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CustomerCsvJob {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CustomerRepository customerRepository;

    @Bean
    public FlatFileItemReader<Customer> csvReader() {
        FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource("src/main/resources/customers.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(csvLineMapper());
        return reader;
    }

    private LineMapper<Customer> csvLineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public RepositoryItemWriter<Customer> csvToDatabaseWriter() {
        return new RepositoryItemWriter<Customer>() {{
            setRepository(customerRepository);
            setMethodName("save");
        }};
    }

    @Bean
    public Step csvToDatabaseStep(StepLoggingListener listener) {
        return new StepBuilder("csvToDatabaseStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(csvReader())
                .processor(new CustomerProcessor())
                .writer(csvToDatabaseWriter())
                .listener(listener)
                .build();
    }

    @Bean
    public Job importCustomersJob(Step csvToDatabaseStep) {
        return new JobBuilder("importCustomersJob", jobRepository)
                .start(csvToDatabaseStep)
                .build();
    }
}
