package com.mindbowser.stripe.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mindbowser.stripe.constant.ExceptionConstant;
import com.mindbowser.stripe.entity.Customers;
import com.mindbowser.stripe.repo.CoustomerRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	CoustomerRepo coustomerRepo;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customers user = coustomerRepo.findByEmailAndIsDeleted(username, false)
				.orElseThrow(() -> new UsernameNotFoundException(ExceptionConstant.EXC_USER_NOT_FOUND));

		return UserDetailsImpl.build(user);
	}

}