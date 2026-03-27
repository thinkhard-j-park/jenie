package org.jenie.spring.helloworld.annotation;

import org.jspecify.annotations.NonNull;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public final class AppType {

	private AppType() {
	}

	public static class ImperativeCondition implements Condition {

		@Override
		public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
			return "sync".equalsIgnoreCase(context.getEnvironment().getProperty("mongodb.setting.type", "sync"));
		}

	}

	public static class ReactiveCondition implements Condition {

		@Override
		public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
			return "reactive".equalsIgnoreCase(context.getEnvironment().getProperty("mongodb.setting.type", "sync"));
		}

	}

}
