package com.example.jwt.service;

import com.example.jwt.model.CustomerOrder;
import com.example.jwt.model.User;
import com.example.jwt.model.UserPrincipal;
import com.example.jwt.repo.CustomerOrderRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomerOrderService {

    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderService(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    public List<CustomerOrder> getAllCustomerOrdersBasedOnUserNameFromLogin() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal userPrincipal) {
            User user = userPrincipal.getUser();
            return customerOrderRepository.findByCustomerName(user.getUserName().toLowerCase());
        }
        return Collections.emptyList();
    }

    public List<CustomerOrder> getAllCustomerOrders() {
        return customerOrderRepository.findAll();
    }

    public CustomerOrder findByOrderId(String orderId) {
        return customerOrderRepository.findByOrderId(orderId);
    }

    public CustomerOrder addCustomerOrder(CustomerOrder customerOrder) {
        return customerOrderRepository.save(customerOrder);
    }
}
