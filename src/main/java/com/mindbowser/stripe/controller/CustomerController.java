package com.mindbowser.stripe.controller;

import java.io.IOException;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mindbowser.stripe.constant.MessageConstant;
import com.mindbowser.stripe.constant.RolesConstant;
import com.mindbowser.stripe.constant.StaticKey;
import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.model.CardModel;
import com.mindbowser.stripe.model.CustomerModel;
import com.mindbowser.stripe.model.ResponseModel;
import com.mindbowser.stripe.model.SubscriptionModel;
import com.mindbowser.stripe.service.CustomerService;
import com.stripe.exception.StripeException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/customer")
@Api(value = "/customer")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "Service not found"), @ApiResponse(code = 200, message = "Success") })
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private Environment environment;

	/**
	 * Create Customer
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */

	@PostMapping("/create")
	@PreAuthorize(RolesConstant.ADMIN)
	@ApiOperation(value = "Create Customer.", notes = "This API used to Create Customer.")
	public ResponseEntity<ResponseModel> createCustomer(@Valid @RequestBody CustomerModel model)
			throws CustomException {
		log.info("------------ CREATE CUSTOMER [web service] --------------");
		ResponseModel response = ResponseModel.getInstance();

		response.setData(customerService.createCoustomer(model));
		response.setMessage(environment.getProperty(MessageConstant.SUCCESSFULLY_CUSTOMER_CREATED));
		response.setStatusCode(HttpStatus.SC_OK);

		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);

	}

	/**
	 * Create Customer
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */

	@PostMapping("/card")
	@PreAuthorize(RolesConstant.ADMIN)
	@ApiOperation(value = "Add Customer card.", notes = "This API used to Add Customer card.")
	public ResponseEntity<ResponseModel> addCard(@Valid @RequestBody CardModel model) throws CustomException {
		log.info("------------ ADD  CUSTOMER  CARD [web service] --------------");
		ResponseModel response = ResponseModel.getInstance();

		response.setData(customerService.addCoustomerCard(model));
		response.setMessage(environment.getProperty(MessageConstant.SUCCESSFULLY_CARD_CREATED));
		response.setStatusCode(HttpStatus.SC_OK);

		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);

	}

	/**
	 * get card Customer
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */

	@GetMapping("/get-card")
	@PreAuthorize(RolesConstant.ADMIN)
	@ApiOperation(value = "get Customer card.", notes = "This API used to get Customer card.")
	public ResponseEntity<ResponseModel> getCard(@RequestParam(required = true) Integer currentPage,
			@RequestParam(required = true) Integer totalPerPage) throws CustomException {
		log.info("------------ GET  CUSTOMER  CARD [web service] --------------");
		ResponseModel response = ResponseModel.getInstance();

		response.setData(customerService.getCoustomerCard(currentPage, totalPerPage));
		response.setMessage(environment.getProperty(MessageConstant.SUCCESSFULLY_CARD_GET));
		response.setStatusCode(HttpStatus.SC_OK);

		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);

	}

	/**
	 * Add subscription of Customer
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */
	@PostMapping("/subscription")
	@PreAuthorize(RolesConstant.ADMIN)
	@ApiOperation(value = "Subscription", response = ResponseModel.class, notes = "This API is used to  create subscription")
	public ResponseEntity<ResponseModel> createSubscription(@RequestBody SubscriptionModel model)
			throws CustomException, StripeException, IOException {
		log.info("----------------- SUBSCRIPTION ---------------");
		ResponseModel response = ResponseModel.getInstance();

		response.setData(customerService.createSubscription(model));
		response.setMessage(environment.getProperty(MessageConstant.SUCCESSFULLY_SUBSCRIPTION_CREATED));
		response.setStatusCode(HttpStatus.SC_OK);
		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
	}

	/**
	 * Webhook saved paid invoice in db
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */
	@PostMapping("/webhook")
	@PermitAll
	@ApiOperation(value = "webhook", response = ResponseModel.class, notes = "This API is used to  stripe webhook.")
	public ResponseEntity<ResponseModel> webhook(@Context HttpServletRequest request)
			throws CustomException, StripeException, IOException {
		log.info("----------------- WEBHOOK ---------------");
		ResponseModel response = ResponseModel.getInstance();
		customerService.stripeWebhook(request);
		response.setMessage(StaticKey.SUCCESS);
		response.setStatusCode(HttpStatus.SC_OK);
		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);
	}

}
