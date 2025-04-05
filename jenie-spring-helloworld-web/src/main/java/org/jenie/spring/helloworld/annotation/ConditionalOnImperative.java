package org.jenie.spring.helloworld.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.context.annotation.Conditional;

@Retention(RetentionPolicy.RUNTIME)
@Conditional(AppType.ImperativeCondition.class)
public @interface ConditionalOnImperative {

}
