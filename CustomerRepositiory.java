package com.fisglobal.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fisglobal.model.Customer;

public interface CustomerRepositiory extends JpaRepository<Customer, Long> {

	Optional<Customer> findByEmail(String email);

	@Query("SELECT c FROM Customer c JOIN FETCH c.accounts WHERE c.id = :id")
	Optional<Customer> fetchCustomerWithAccounts(@Param("id") Long id);

}
