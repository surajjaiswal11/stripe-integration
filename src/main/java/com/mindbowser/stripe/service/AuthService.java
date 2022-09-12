package com.mindbowser.stripe.service;

import javax.validation.Valid;

import com.mindbowser.stripe.entity.Customers;
import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.model.CustomerModel;

public interface AuthService {

	Object login(CustomerModel model) throws CustomException;

	Customers signUp(@Valid CustomerModel model) throws CustomException;


}
