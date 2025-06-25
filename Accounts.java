package com.fisglobal.model;

import java.math.BigDecimal;

import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

public class Accounts {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
	@SequenceGenerator(name = "account_seq", sequenceName = "account_sequence", allocationSize = 1)
	private Long accountId;

	private String accountNumber;
	private BigDecimal balance;
	private String accountType; // e.g., SAVINGS, CURRENT
	private String currency;

	private boolean isActive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Accounts() {

	}

	public Accounts(Long accountId, String accountNumber, BigDecimal balance, String accountType, boolean isActive,
			Customer customer) {
		super();
		this.accountId = accountId;
		this.accountNumber = accountNumber;
		this.balance = balance;
		this.accountType = accountType;
		this.isActive = isActive;
		this.customer = customer;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return "Accounts [accountId=" + accountId + ", accountNumber=" + accountNumber + ", balance=" + balance
				+ ", accountType=" + accountType + ", isActive=" + isActive + ", customer=" + customer + "]";
	}

}
