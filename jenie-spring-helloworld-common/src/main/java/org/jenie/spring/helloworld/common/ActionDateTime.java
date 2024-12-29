package org.jenie.spring.helloworld.common;

import java.time.ZonedDateTime;

import org.jenie.spring.helloworld.utils.ZdtUtil;

public class ActionDateTime {

	private ZonedDateTime createdAt = ZonedDateTime.now();

	private ZonedDateTime updatedAt;

	private ZonedDateTime deletedAt;

	public static ActionDateTimeMessage toProtoMessage(ActionDateTime actionDateTime) {
		if (actionDateTime == null) {
			return null;
		}

		var builder = ActionDateTimeMessage.newBuilder();

		if (actionDateTime.getCreatedAt() != null) {
			builder.setCreatedAt(ZdtUtil.toTimestamp(actionDateTime.getCreatedAt()));
		}

		if (actionDateTime.getUpdatedAt() != null) {
			builder.setUpdatedAt(ZdtUtil.toTimestamp(actionDateTime.getUpdatedAt()));
		}

		if (actionDateTime.getDeletedAt() != null) {
			builder.setDeletedAt(ZdtUtil.toTimestamp(actionDateTime.getDeletedAt()));
		}

		return builder.build();
	}

	public static ActionDateTime fromProtoMessage(ActionDateTimeMessage protoMessage) {
		if (protoMessage == null) {
			return null;
		}

		var actionDateTime = new ActionDateTime();
		actionDateTime.setCreatedAt(ZdtUtil.fromTimestamp(protoMessage.getCreatedAt()));
		actionDateTime.setUpdatedAt(ZdtUtil.fromTimestamp(protoMessage.getUpdatedAt()));
		actionDateTime.setDeletedAt(ZdtUtil.fromTimestamp(protoMessage.getDeletedAt()));

		return actionDateTime;

	}

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
