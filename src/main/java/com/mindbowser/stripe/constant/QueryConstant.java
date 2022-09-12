package com.mindbowser.stripe.constant;

public class QueryConstant {

	private QueryConstant() {
	}

	public static final String SELECT = "SELECT ";
	public static final String STAR = " * ";
	public static final String COUNT = " COUNT(*) ";
	public static final String FROM = " FROM stripDemo.cards where ";
	public static final String IS_DELETED_AND_ADDED_BY_AND_ORDER_BY_ID_DESC = " is_deleted =?2 and customer_id =?1 order by id desc ";
	public static final String LIMIT = " LIMIT ?3,?4";
	public static final String AND = " and ";

}
