package com.mindbowser.stripe.config.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindbowser.stripe.constant.StaticKey;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		log.error("Unauthorized error: {}", authException.getMessage());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		final Map<String, Object> body = new HashMap<>();
		body.put(StaticKey.STATUS, HttpServletResponse.SC_UNAUTHORIZED);
		body.put(StaticKey.ERROR, StaticKey.UNAUTHORIZED);
		body.put(StaticKey.MESSAGE, authException.getMessage());
		body.put(StaticKey.PATH, request.getServletPath());

		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}

}
