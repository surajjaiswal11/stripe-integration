package com.mindbowser.stripe.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "invoice")
public class Invoice extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String invoiceId;
	private String invoicePdf;
	private String quantity;
	private String amount;
	private String customerId;

}
