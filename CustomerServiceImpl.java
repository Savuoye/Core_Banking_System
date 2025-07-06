package com.fisglobal.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fisglobal.model.Customer;
import com.fisglobal.repositories.CustomerRepository;

public class CustomerServiceImpl {

	@Autowired
	private CustomerRepository customerRepository;

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	public List<Customer> getAllCustomer() {
		logger.info("Fetching all Customers");
		return customerRepository.findAll();

	}

	public Customer createCustomer(Customer customer) {
		logger.info("Creating new Customer {}", customer.getFullName());
		return customerRepository.save(customer);
	}

	public Customer getCustomerById(Long customerId) {
		logger.info("Fetching customer with ID: {}", customerId);
		return customerRepository.findById(customerId).orElseThrow(() -> {
			logger.warn("Customer not found: {}", customerId);
			return new RuntimeException("Customer not found");
		});
	}

}
