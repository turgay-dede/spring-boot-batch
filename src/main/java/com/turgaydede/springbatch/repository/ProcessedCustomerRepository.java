package com.turgaydede.springbatch.repository;

import com.turgaydede.springbatch.entity.ProcessedCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedCustomerRepository extends JpaRepository<ProcessedCustomer,Integer> {
}
