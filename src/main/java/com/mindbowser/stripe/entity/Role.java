package com.mindbowser.stripe.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "name")
	private String name;
}
