package com.mindbowser.stripe.model;

import javax.validation.constraints.NotBlank;

import com.mindbowser.stripe.constant.StaticKey;

import lombok.Data;

@Data
public class CardModel {
	
	@NotBlank(message = StaticKey.SHOULD_NOT_BE_BLANK)
	private String customerId;

	@NotBlank(message = StaticKey.SHOULD_NOT_BE_BLANK)
	private String cardNumber;

	@NotBlank(message = StaticKey.SHOULD_NOT_BE_BLANK)
	private String cardExpMonth;

	@NotBlank(message = StaticKey.SHOULD_NOT_BE_BLANK)
	private String cardExpYear;

	@NotBlank(message = StaticKey.SHOULD_NOT_BE_BLANK)
	private String cardCvc;

}
