package com.mindbowser.stripe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.mindbowser.*")
@SpringBootApplication
@PropertySources(value = { @PropertySource("classpath:message.properties"),
		@PropertySource("classpath:exception.properties"),
		@PropertySource("classpath:profiles/${spring.profiles.active}/application.properties") })
public class StripeIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StripeIntegrationApplication.class, args);
	}

}
