package com.mindbowser.stripe.model;

import org.apache.http.HttpStatus;

import lombok.Data;

@Data
public class ResponseModel {

	private String error;
	private Object data;
	private Integer statusCode;
	private String message;


	public static ResponseModel getInstance() {
		ResponseModel response = new ResponseModel();
		response.setStatusCode(HttpStatus.SC_OK);
		return response;
	}

}
