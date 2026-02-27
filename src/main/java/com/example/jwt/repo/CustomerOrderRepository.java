package com.example.jwt.repo;


import com.example.jwt.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {

    CustomerOrder findByOrderId(String orderId);

    List<CustomerOrder> findByCustomerName(String customerName);
}
