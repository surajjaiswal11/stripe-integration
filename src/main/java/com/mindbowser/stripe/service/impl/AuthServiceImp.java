package com.mindbowser.stripe.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.mindbowser.stripe.config.jwt.JwtResponse;
import com.mindbowser.stripe.config.jwt.JwtUtils;
import com.mindbowser.stripe.constant.ExceptionConstant;
import com.mindbowser.stripe.constant.StaticKey;
import com.mindbowser.stripe.entity.Customers;
import com.mindbowser.stripe.entity.Role;
import com.mindbowser.stripe.entity.RoleModel;
import com.mindbowser.stripe.exception.CustomException;
import com.mindbowser.stripe.model.CustomerModel;
import com.mindbowser.stripe.repo.CoustomerRepo;
import com.mindbowser.stripe.repo.RoleRepository;
import com.mindbowser.stripe.service.AuthService;
import com.mindbowser.stripe.util.CustomDozerHelper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImp implements AuthService {

	private Mapper mapper = new DozerBeanMapper();

	@Autowired
	private Environment environment;

	@Autowired
	private CoustomerRepo coustomerRepo;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public Customers signUp(@Valid CustomerModel model) throws CustomException {

		checkSignUpCustomerRole(model);
		checkCustomerExiestOrNot(model);

		if (model.getPassword() != null) {
			model.setPassword(encoder.encode(model.getPassword()));
		}
		return coustomerRepo.save(mapper.map(model, Customers.class));

	}

	@Override
	public Object login(CustomerModel model) throws CustomException {
		validateLoginObject(model);
		Customers customer = coustomerRepo.findByEmailAndIsDeleted(model.getEmail(), false)
				.orElseThrow(() -> new CustomException(environment.getProperty(ExceptionConstant.EXC_USER_NOT_FOUND)));
		try {
			if (null != customer) {
				checkUserPassword(model, customer);
				Authentication authentication = authenticationManager
						.authenticate(new UsernamePasswordAuthenticationToken(model.getEmail(), model.getPassword()));

				SecurityContextHolder.getContext().setAuthentication(authentication);
				String jwt = jwtUtils.generateJwtToken(authentication);

				UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
				List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList());

				return new JwtResponse(jwt, StaticKey.BEARER, userDetails.getId(), userDetails.getUsername(), roles);
			}
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				throw new CustomException(environment.getProperty(ExceptionConstant.EXC_INVALID_CREDENTIALS));
			}

			else {
				throw new CustomException(environment.getProperty(ExceptionConstant.EXC_SOMETHING_WENT_WRONG));

			}
		}
		return null;

	}

	/**
	 * Check login user model.
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @throws CustomException
	 * @since Aug 29, 2020
	 * 
	 */
	public void validateLoginObject(CustomerModel userModel) throws CustomException {
		if (userModel.getEmail() == null || (userModel.getPassword() == null)) {
			throwEmptyFieldError();
		}

	}

	/**
	 * Common method to throw field empty error.
	 * 
	 * @author Mindbowser | suraj.jaiswal@mindbowser.com
	 * @since Aug 29, 2020
	 * @throws CustomException
	 */
	public void throwEmptyFieldError() throws CustomException {
		log.error(environment.getProperty(ExceptionConstant.EXC_MISSING_PARAMETERS));
		throw new CustomException(environment.getProperty(ExceptionConstant.EXC_MISSING_PARAMETERS));
	}

	/**
	 * Check if user with given role already exists on sign up.
	 *
	 * @param userModel
	 * @throws CustomException
	 */
	private void checkSignUpCustomerRole(CustomerModel model) throws CustomException {

		Set<Role> roles = roleRepository.findByName(model.getRoles().iterator().next().getName());
		if (roles.isEmpty()) {
			throw new CustomException(environment.getProperty(ExceptionConstant.ROLE_NOT_FOUND), HttpStatus.NOT_FOUND);
		}

		model.setRoles(CustomDozerHelper.setMap(mapper, roles, RoleModel.class));
	}

	private void checkUserPassword(CustomerModel customerModel, Customers customer) {
		if (!new BCryptPasswordEncoder().matches(customerModel.getPassword(), customer.getPassword())) {
			throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
		}
	}

	/**
	 * Check if user with given manager already exists on sign up.
	 *
	 * @param userModel
	 * @throws CustomException
	 */
	private void checkCustomerExiestOrNot(CustomerModel model) throws CustomException {
		Optional<Customers> manager = coustomerRepo.findByEmailAndIsDeleted(model.getEmail(), false);
		if (manager.isPresent()) {
			throw new CustomException(environment.getProperty(ExceptionConstant.USER_ALREADY_EXISTS));
		}
	}

}
