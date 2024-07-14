package org.jenie.spring.helloworld.entity;

import org.springframework.data.annotation.Id;

public class Person {
	@Id
	private String id;
	private String firstName;
	private String lastName;

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "Person{" +
				"id='" + this.id + '\'' +
				", firstName='" + this.firstName + '\'' +
				", lastName='" + this.lastName + '\'' +
				'}';
		//@formatter:on
	}
}
