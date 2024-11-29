package com.turgaydede.springbatch.processor;
import com.turgaydede.springbatch.entity.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomerProcessor implements ItemProcessor<Customer,Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        if(customer.getGender().toUpperCase().equals("MALE")) {
            return customer;
        }else{
            return null;
        }
    }
}
