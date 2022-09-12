package com.mindbowser.stripe.model;

import com.mindbowser.stripe.entity.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class SubscriptionModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	private String customerId;

	private String priceId;

}
