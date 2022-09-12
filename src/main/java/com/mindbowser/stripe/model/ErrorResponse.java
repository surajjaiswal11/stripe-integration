package com.mindbowser.stripe.model;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

	private Date timestamp;
	private String message;
	private List<String> details;
	private int statusCode;

}