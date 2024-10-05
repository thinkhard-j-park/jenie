package org.jenie.spring.helloworld.entity;

import org.jenie.spring.helloworld.dto.SortCode;

import org.springframework.data.domain.Sort;

public enum SortOrder {

	//@formatter:off
	TIME_DESC(SortCode.TIME_DESC, Sort.Direction.DESC, "_id"),
	TIME_ASC(SortCode.TIME_ASC, Sort.Direction.ASC, "_id");
	//@formatter:on

	final SortCode sortCode;

	final Sort.Direction direction;

	final String field;

	SortOrder(SortCode sortCode, Sort.Direction direction, String field) {
		this.sortCode = sortCode;
		this.direction = direction;
		this.field = field;
	}

	public static SortOrder fromValue(int code) {
		for (SortOrder sortOrder : SortOrder.values()) {
			if (code == sortOrder.sortCode.getCode()) {
				return sortOrder;
			}
		}
		return SortOrder.TIME_DESC;
	}

	public SortCode getSortCode() {
		return this.sortCode;
	}

	public Sort.Direction getDirection() {
		return this.direction;
	}

	public String getField() {
		return this.field;
	}

}
