package org.jenie.spring.helloworld.common;

public enum ArticleState {

	Unknown(-9), Deleted(-1), Normal(0);

	final int code;

	ArticleState(int code) {
		this.code = code;
	}

	public static ArticleState fromCode(int state) {
		for (ArticleState articleState : values()) {
			if (articleState.getCode() == state) {
				return articleState;
			}
		}
		return Unknown;
	}

	public int getCode() {
		return this.code;
	}

}
