package com.mindbowser.stripe.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindbowser.stripe.entity.Customers;

@Repository
public interface CoustomerRepo extends JpaRepository<Customers, Long> {

	Optional<Customers> findByEmailAndIsDeleted(String username, boolean b);

	Customers findByStripeCustomerId(String customerId);

}
