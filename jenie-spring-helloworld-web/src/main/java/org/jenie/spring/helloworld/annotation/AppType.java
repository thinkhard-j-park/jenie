package org.jenie.spring.helloworld.annotation;

import java.util.Objects;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

public final class AppType {

	private AppType() {
	}

	public static boolean isClassPresent(String className, ConditionContext context) {
		try {
			Objects.requireNonNull(context.getClassLoader()).loadClass(className);
			return true;
		}
		catch (ClassNotFoundException ex) {
			return false;
		}
	}

	public static class ImperativeCondition implements Condition {

		@Override
		public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
			return isClassPresent("com.mongodb.client.MongoClient", context);
		}

	}

	public static class ReactiveCondition implements Condition {

		@Override
		public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
			return isClassPresent("com.mongodb.reactivestreams.client.MongoClient", context);
		}

	}

}
