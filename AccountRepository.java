package com.fisglobal.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisglobal.model.Accounts;

public interface AccountRepository extends JpaRepository<Accounts, Long> {

	/* List<Accounts> findByCustomerId(Long customerId); */

	/*
	 * List<Accounts> findByAccountTypeAndIsActive(String accountType, boolean
	 * isActive);
	 */

	/*
	 * @Query("SELECT a FROM Account a WHERE a.currency = :currency AND a.balance > :minBalance"
	 * ) List<Accounts> findHighValueAccounts(@Param("currency") String currency,
	 * 
	 * @Param("minBalance") BigDecimal minBalance);
	 * 
	 */

	List<Accounts> findByCustomerCustId(Long CustomerId);

}
