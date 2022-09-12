package com.mindbowser.stripe.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class RoleModel extends BaseModel {
	private static final long serialVersionUID = 1L;

	private String name;

}
