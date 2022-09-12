package com.mindbowser.stripe.constant;

public class ExceptionConstant {

	private ExceptionConstant() {
	}

	public static final String USER_ALREADY_EXISTS = "user.already.exists";
	public static final String EXC_MISSING_PARAMETERS = "missing.parameter";
	public static final String EXC_USER_NOT_FOUND = "user.not.found";
	public static final String EXC_INVALID_CREDENTIALS = "invalid.credentials";
	public static final String EXC_SOMETHING_WENT_WRONG = "something.went.wrong";
	public static final String EMPLOYEE_CAN_NOT_BE_CHANGE_EMAIL = "email.can.not.change";
	public static final String CURRENT_LOGGED_USER_NOT_FOUND = "current.logged.user.not.found";
	public static final String ROLE_NOT_FOUND = "role.not.found";
	public static final String JWT_TOKEN_UNSUPPORTED = "jwt.token.unsupported";
	public static final String JWT_TOKEN_EXPIRED = "jwt.token.expired";
	public static final String INVALID_JWT_TOKEN = "invalid.jwt.token";
	public static final String INVALID_JWT_SIGNATURE = "invalid.jwt.signature";

}
