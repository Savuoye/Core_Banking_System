package com.fisglobal.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Accounts {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
	@SequenceGenerator(name = "account_seq", sequenceName = "account_sequence", allocationSize = 1)
	@Column(name = "accountId")
	private Long accountId;

	@Column(name = "accountNo")
	private String accountNo;

	@Column(name = "balance")
	private BigDecimal balance;

	@Column(name = "accountType")
	private String accountType; // e.g., SAVINGS, CURRENT

	@Column(name = "currency")
	private String currency;

	@Column(name = "isActive")
	private boolean isActive;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Accounts() {

	}

	public Accounts(Long accountId, String accountNo, BigDecimal balance, String accountType, String currency,
			boolean isActive, Customer customer) {
		super();
		this.accountId = accountId;
		this.accountNo = accountNo;
		this.balance = balance;
		this.accountType = accountType;
		this.currency = currency;
		this.isActive = isActive;
		this.customer = customer;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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
		return "Accounts [accountId=" + accountId + ", accountNo=" + accountNo + ", balance=" + balance
				+ ", accountType=" + accountType + ", currency=" + currency + ", isActive=" + isActive + ", customer="
				+ customer + "]";
	}

}
