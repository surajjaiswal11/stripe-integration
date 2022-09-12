package com.mindbowser.stripe.entity;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
public class Cards extends BaseEntity {
	private static final long serialVersionUID = 1L;
	private String cardDigit;
	private String cardType;

}
