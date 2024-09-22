package org.jenie.spring.data.mongodb.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Transactional
public @interface MongoKeyBasedTransactional {

	@AliasFor(annotation = Transactional.class, attribute = "value")
	String transactionManager() default "";

	@AliasFor(annotation = Transactional.class, attribute = "propagation")
	Propagation propagation() default Propagation.REQUIRED;

	@AliasFor(annotation = Transactional.class, attribute = "isolation")
	Isolation isolation() default Isolation.DEFAULT;

	@AliasFor(annotation = Transactional.class, attribute = "timeout")
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	@AliasFor(annotation = Transactional.class, attribute = "readOnly")
	boolean readOnly() default false;

	@AliasFor(annotation = Transactional.class, attribute = "rollbackFor")
	Class<? extends Throwable>[] rollbackFor() default {};

	@AliasFor(annotation = Transactional.class, attribute = "noRollbackFor")
	Class<? extends Throwable>[] noRollbackFor() default {};

	String key() default "";

}

