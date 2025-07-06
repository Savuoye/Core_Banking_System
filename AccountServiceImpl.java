package com.fisglobal.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fisglobal.model.Accounts;
import com.fisglobal.model.Customer;
import com.fisglobal.repositories.AccountRepository;
import com.fisglobal.repositories.CustomerRepository;

import jakarta.transaction.Transactional;

@Service
public class AccountServiceImpl {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private AccountRepository accountRepository;

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	@Transactional
	public Accounts openAccounts(Accounts accounts) {
		Long customerId = accounts.getCustomer().getCustomerId();
		logger.info("Opening account for customerId: {}", customerId);

		Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
			logger.warn("Customer not found: {}", customerId);
			return new RuntimeException("Customer not found");
		});

		accounts.setCustomer(customer);
		accounts.setAccountNo(UUID.randomUUID().toString());

		String accountType = accounts.getAccountType();
		if (accountType == null || accountType.equalsIgnoreCase("SAVINGS") || accountType.equalsIgnoreCase("CURRENT")) {
			accounts.setAccountType(accountType.toUpperCase());
		}

		String currencyType = accounts.getCurrency();
		if (currencyType == null || !(currencyType.equalsIgnoreCase("INR") || (currencyType.equalsIgnoreCase("USD"))
				|| currencyType.equalsIgnoreCase("EURO"))) {
			accounts.setCurrency(currencyType.toUpperCase());
		}
		accounts.setActive(true);

		Accounts saveData = accountRepository.save(accounts);
		logger.info("Account created: {} for customer {}", saveData.getAccountNo(), customer.getFullName());

		return saveData;
	}

	public List<Accounts> getAccountsByCustomerId(Long customerId) {
		logger.info("Fetching accounts for customerId: {}", customerId);
		List<Accounts> accounts = accountRepository.findByCustomerCustId(customerId);

		if (accounts.isEmpty()) {
			logger.warn("No accounts found for customerId: {}", customerId);
		} else {
			logger.info("Found {} account(s) for customerId: {}", accounts.size(), customerId);
		}

		return accounts;
	}
}
