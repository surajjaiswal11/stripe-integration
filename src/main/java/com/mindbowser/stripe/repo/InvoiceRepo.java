package com.mindbowser.stripe.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindbowser.stripe.entity.Invoice;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long> {

}
