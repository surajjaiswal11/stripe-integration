package com.mindbowser.stripe.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mindbowser.stripe.constant.StaticKey;
import com.mindbowser.stripe.service.impl.UserDetailsServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
				String email = jwtUtils.getEmailFromToken(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {

			log.error("Cannot set user authentication " + e.getMessage());
		}

		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {

		String headerAuth = request.getHeader(StaticKey.AUTHORIZATION);

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(StaticKey.BEARER)) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}
}
