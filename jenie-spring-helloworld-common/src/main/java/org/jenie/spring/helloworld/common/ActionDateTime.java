package org.jenie.spring.helloworld.common;

import java.time.ZonedDateTime;

public class ActionDateTime {

	private ZonedDateTime createdAt = ZonedDateTime.now();

	private ZonedDateTime updatedAt;

	private ZonedDateTime deletedAt;

	public ZonedDateTime getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ZonedDateTime getDeletedAt() {
		return this.deletedAt;
	}

	public void setDeletedAt(ZonedDateTime deletedAt) {
		this.deletedAt = deletedAt;
	}

	@Override
	public String toString() {
		//@formatter:off
		return "ActionDateTime{" +
				"createdAt=" + this.createdAt +
				", updatedAt=" + this.updatedAt +
				", deletedAt=" + this.deletedAt +
				'}';
		//@formatter:on
	}
}
