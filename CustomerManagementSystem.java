package com.fisglobal.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fisglobal.model.Accounts;
import com.fisglobal.model.Customer;
import com.fisglobal.service.AccountServiceImpl;
import com.fisglobal.service.CustomerServiceImpl;

@RestController
@RequestMapping("/api/customerManagement")
public class CustomerManagementSystem {

	@Autowired
	private AccountServiceImpl accountService;

	@Autowired
	private CustomerServiceImpl customerService;

	private static final Logger logger = LoggerFactory.getLogger(CustomerManagementSystem.class);

	@RequestMapping(value = "/open", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Accounts> openAccount(@RequestBody Accounts accounts) {

		logger.debug("Received request to open Accounts: {}", accounts.getCustomer());

		Accounts newAccount = accountService.openAccounts(accounts);
		return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
		logger.debug("Received request to create customer: {}", customer.getFullName());

		Customer createCustomer = customerService.createCustomer(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(createCustomer);

	}

	@RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<Accounts>> getAccountsByCustomerId(@PathVariable Long customerId) {
		List<Accounts> accounts = accountService.getAccountsByCustomerId(customerId);
		return ResponseEntity.ok(accounts);

	}

}
