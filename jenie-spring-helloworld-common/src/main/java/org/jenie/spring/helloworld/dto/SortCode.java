package org.jenie.spring.helloworld.dto;

public enum SortCode {

	//@formatter:off
	TIME_DESC(0,  "Time desc"),
	TIME_ASC(-1, "Time asc");
	//@formatter:on

	final int code;

	final String desc;

	SortCode(int value, String desc) {
		this.code = value;
		this.desc = desc;
	}

	public static SortCode fromCode(int code) {
		for (SortCode sortCode : SortCode.values()) {
			if (sortCode.code == code) {
				return sortCode;
			}
		}
		return null;
	}

	public int getCode() {
		return this.code;
	}

	public String getDesc() {
		return this.desc;
	}

}
