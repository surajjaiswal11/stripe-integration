package com.mindbowser.stripe.config.jwt;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.mindbowser.stripe.constant.ExceptionConstant;
import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.service.impl.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {

	@Value("${mindbowser.jwtSecret.key}")
	private String jwtSecret;

	@Value("${mindbowser.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	private Environment environment;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public boolean validateJwtToken(String authToken) throws CustomException {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			log.error("Invalid JWT signature: {}", e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.INVALID_JWT_SIGNATURE));

		} catch (MalformedJwtException e) {
			log.error("Invalid JWT token: {}", e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.INVALID_JWT_TOKEN));

		} catch (ExpiredJwtException e) {
			log.error("JWT token is expired: {}", e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.JWT_TOKEN_EXPIRED));

		} catch (UnsupportedJwtException e) {
			log.error("JWT token is unsupported: {}", e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.JWT_TOKEN_UNSUPPORTED));

		} catch (Exception e) {
			log.error("JWT claims string is empty: {}", e.getMessage());
			throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));

		}

	}

	/**
	 * Extract email from token
	 * 
	 * @author mindbowser | suraj.jaiswal@mindbowser.com
	 * @since 29-aug-2022
	 * @param token
	 * @return
	 */
	public String getEmailFromToken(String token) {

		return getClaimFromToken(token, Claims::getSubject);
	}

	/**
	 * Get claim from token
	 * 
	 * @author mindbowser | suraj.jaiswal@mindbowser.com
	 * @since 29-aug-2022
	 * @param <T>
	 * @param token
	 * @param claimsResolver
	 * @return
	 */
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Get all claims from token
	 * 
	 * @author mindbowser | suraj.jaiswal@mindbowser.com
	 * @since 29-aug-2022
	 * @param token
	 * @return
	 */

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
	}
}