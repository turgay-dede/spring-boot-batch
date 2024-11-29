package com.turgaydede.springbatch.processor;

import com.turgaydede.springbatch.entity.Customer;
import com.turgaydede.springbatch.entity.ProcessedCustomer;
import com.turgaydede.springbatch.repository.CustomerRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DatabaseProcessor implements ItemProcessor<Customer, ProcessedCustomer> {

    private final CustomerRepository customerRepository;

    public DatabaseProcessor(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public ProcessedCustomer process(Customer customer) throws Exception {
        ProcessedCustomer processedCustomer = new ProcessedCustomer();
        processedCustomer.setFullName(customer.getFirstName() + " " + customer.getLastName());
        processedCustomer.setEmail(customer.getEmail().toLowerCase());
        processedCustomer.setCountry(customer.getCountry().toUpperCase());

        customer.setIsProcessed(true);
        customerRepository.save(customer);

        return processedCustomer;
    }
}
