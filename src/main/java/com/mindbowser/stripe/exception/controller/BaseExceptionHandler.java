package com.mindbowser.stripe.exception.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mindbowser.stripe.model.ErrorResponse;
import com.mindbowser.stripe.model.ResponseModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestControllerAdvice
public class BaseExceptionHandler {
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseModel> handleValidationExceptions(MethodArgumentNotValidException ex) {
		log.info("------------ handleValidationExceptions [web service] --------------");
		ResponseModel response = ResponseModel.getInstance();
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> {
			if (errors.containsKey(error.getField())) {

				errors.put(error.getField(),
						String.format("%s, %s", errors.get(error.getField()), error.getDefaultMessage()));
			} else {

				errors.put(error.getField(), error.getDefaultMessage());

			}
			response.setData(errors);

		});
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(value = { NotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse unKnownException(Exception ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getMessage());
		return new ErrorResponse(new Date(), "Not found", details, HttpStatus.NOT_FOUND.value());
	}

	@ExceptionHandler(value = { AccessDeniedException.class })
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse accessDeniedException(Exception ex) {
		List<String> details = new ArrayList<>();
		details.add(ex.getMessage());
		return new ErrorResponse(new Date(), "Access denied", details, HttpStatus.FORBIDDEN.value());
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse internalServerEcecption(Exception ex, HttpServletRequest request,
			HttpServletResponse response) {
		List<String> details = new ArrayList<>();
		if (ex instanceof NullPointerException) {

			details.add(ex.getMessage());
			return new ErrorResponse(new Date(), "Null Pointer", details, HttpStatus.BAD_REQUEST.value());

		}
		details.add(ex.getMessage());
		return new ErrorResponse(new Date(), "Internal server error", details,
				HttpStatus.INTERNAL_SERVER_ERROR.value());

	}


}