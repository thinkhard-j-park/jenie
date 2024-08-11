package org.jenie.spring.helloworld.entity;

import org.springframework.data.domain.Sort;

public enum SortOrder {

	//@formatter:off
	TIME_DESC(0, Sort.Direction.DESC, "_id", "Time desc"),
	TIME_ASC(-1, Sort.Direction.ASC, "_id", "Time asc");
	//@formatter:on

	final int value;

	final Sort.Direction direction;

	final String field;

	final String desc;

	SortOrder(int value, Sort.Direction direction, String field, String desc) {
		this.value = value;
		this.direction = direction;
		this.field = field;
		this.desc = desc;
	}

	public static SortOrder fromValue(int code) {
		for (SortOrder sortOrder : SortOrder.values()) {
			if (code == sortOrder.value) {
				return sortOrder;
			}
		}
		return SortOrder.TIME_DESC;
	}

	public int getValue() {
		return this.value;
	}

	public Sort.Direction getDirection() {
		return this.direction;
	}

	public String getField() {
		return this.field;
	}

	public String getDesc() {
		return this.desc;
	}
}
