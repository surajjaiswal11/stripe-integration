package com.mindbowser.stripe.entity;

import java.util.Date;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
public class Subscriptions extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private Date subscriptionStartDate;
	private Date subscriptionEndDate;
	private String type;
	private String rate;
	private String customerId;
}
