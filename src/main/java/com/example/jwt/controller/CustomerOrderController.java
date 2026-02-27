package com.example.jwt.controller;

import com.example.jwt.model.CustomerOrder;
import com.example.jwt.service.CustomerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("orders")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @Autowired
    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN')")
    public List<CustomerOrder> getAllCustomerOrders() {
        return customerOrderService.getAllCustomerOrders();
    }

    @GetMapping("orderId")
    @PreAuthorize("hasAnyRole('USER','SUPER_ADMIN')")
    public CustomerOrder findByOrderId(@RequestParam String orderId) {
        return customerOrderService.findByOrderId(orderId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public CustomerOrder createCustomerOrder(@RequestBody CustomerOrder customerOrder) {
        return customerOrderService.addCustomerOrder(customerOrder);
    }

    @GetMapping("my-orders")
    public List<CustomerOrder> getAllCustomerOrdersBasedOnUserNameFromLogin() {
        return customerOrderService.getAllCustomerOrdersBasedOnUserNameFromLogin();
    }
}
