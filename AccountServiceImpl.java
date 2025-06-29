package com.infotech.service;

import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fisglobal.model.Accounts;
import com.fisglobal.model.Customer;
import com.fisglobal.repositories.AccountRepository;
import com.fisglobal.repositories.CustomerRepositiory;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl {

	@Autowired
	private CustomerRepositiory customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Transactional
	public Accounts openAccount(Accounts accounts) {
		Long customerId = accounts.getCustomer().getCustomerId();
		logger.info("Opening account for customerId: {}", customerId);

		Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
			logger.warn("Customer not found: {}", customerId);
			return new RuntimeException("Customer not found");
		});

		accounts.setCustomer(customer);
		accounts.setAccountNumber(UUID.randomUUID().toString());
		accounts.setActive(true);

		Accounts savedData = accountRepository.save(accounts);
		logger.info("Account created: {} for customer {}", savedData.getAccountNumber(), customer.getFullName());

		return savedData;

	}

}
