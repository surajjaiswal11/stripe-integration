package com.mindbowser.stripe.controller;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mindbowser.stripe.constant.MessageConstant;
import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.model.CustomerModel;
import com.mindbowser.stripe.model.ResponseModel;
import com.mindbowser.stripe.service.AuthService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Api(value = "/api/auth")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@ApiResponses(value = { @ApiResponse(code = 500, message = "Internal Server Error"),
		@ApiResponse(code = 404, message = "Service not found"), @ApiResponse(code = 200, message = "Success") })
public class AuthController {

	@Autowired
	private AuthService authService;

	@Autowired
	private Environment environment;

	/**
	 * Customer signup API.
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */
	@PermitAll
	@PostMapping("/signup")
	@ApiOperation(value = "Sign up Customer.", notes = "This API used to sign up Customer.")
	public ResponseEntity<ResponseModel> createCustomer(@Valid @RequestBody CustomerModel model)
			throws CustomException {
		log.info("------------ In signUp [web service] --------------");
		ResponseModel response = ResponseModel.getInstance();

		authService.signUp(model);
		response.setMessage(environment.getProperty(MessageConstant.MSG_SINGUP_SUCCESS));
		response.setStatusCode(HttpStatus.SC_OK);

		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);

	}

	/**
	 * Login API.
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 */
	@PermitAll
	@PostMapping("/signin")
	@ApiOperation(value = "Login user.", notes = "This API used to login.")
	public ResponseEntity<ResponseModel> login(@RequestBody CustomerModel model) throws CustomException {
		log.info("------------ In login [web service] --------------");
		ResponseModel response = ResponseModel.getInstance();
		response.setData(authService.login(model));
		response.setMessage(environment.getProperty(MessageConstant.MSG_LOGIN_SUCCESS));
		response.setStatusCode(HttpStatus.SC_OK);
		return new ResponseEntity<>(response, org.springframework.http.HttpStatus.OK);

	}

}
