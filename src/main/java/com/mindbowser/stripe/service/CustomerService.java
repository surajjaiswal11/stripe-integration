package com.mindbowser.stripe.service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.model.CardModel;
import com.mindbowser.stripe.model.CustomerModel;
import com.mindbowser.stripe.model.SubscriptionModel;

public interface CustomerService {

	CustomerModel createCoustomer(@Valid CustomerModel model) throws CustomException;

	Object addCoustomerCard(@Valid CardModel model) throws CustomException;

	void stripeWebhook(HttpServletRequest request) throws CustomException;

	Object getCoustomerCard(Integer currentPage, Integer totalPerPage) throws CustomException;

	Object createSubscription(SubscriptionModel model) throws CustomException;

}
