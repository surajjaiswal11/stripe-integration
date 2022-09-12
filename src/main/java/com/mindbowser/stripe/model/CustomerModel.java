package com.mindbowser.stripe.model;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mindbowser.stripe.constant.StaticKey;
import com.mindbowser.stripe.entity.BaseModel;
import com.mindbowser.stripe.entity.RoleModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CustomerModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	@Pattern(regexp = StaticKey.REG_EXPRESSION, message = StaticKey.INVALID_INPUT)
	@NotBlank(message = StaticKey.FIRST_NAME + StaticKey.SHOULD_NOT_BE_BLANK)
	@Size(min = 3, message = StaticKey.FIRST_NAME + StaticKey.SHOULD_BE_AT_LEAST_THREE_CHAR)
	private String firstName;

	@Pattern(regexp = StaticKey.REG_EXPRESSION, message = StaticKey.INVALID_INPUT)
	@NotBlank(message = StaticKey.LAST_NAME + StaticKey.SHOULD_NOT_BE_BLANK)
	@Size(min = 3, message = StaticKey.LAST_NAME + StaticKey.SHOULD_BE_AT_LEAST_THREE_CHAR)
	private String lastName;

	@Past
	@JsonFormat(pattern = "yyyy-MM-dd")
	private Date birthDate;

	@NotBlank(message = StaticKey.ADDRESS + StaticKey.SHOULD_NOT_BE_BLANK)
	@Size(min = 3, message = StaticKey.ADDRESS + StaticKey.SHOULD_BE_AT_LEAST_THREE_CHAR)
	private String address;

	@NotBlank(message = StaticKey.EMAIL + StaticKey.SHOULD_NOT_BE_BLANK)
	@Email
	private String email;

	// @NotBlank(message = StaticKey.PASS_IS_MANDATORY)
	private String password;

	private String description;

	private String city;

	private String state;

	private String country;

	private String zipCode;

	private String phone;

	private Set<RoleModel> roles;

	private String stripeCustomerId;

}
