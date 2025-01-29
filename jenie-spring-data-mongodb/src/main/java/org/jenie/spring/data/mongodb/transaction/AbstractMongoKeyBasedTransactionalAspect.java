package org.jenie.spring.data.mongodb.transaction;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractMongoKeyBasedTransactionalAspect {

	protected String getDBKey(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional) {
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();

		Object[] args = pjp.getArgs();

		for (int i = 0; i < method.getParameterAnnotations().length; i++) {
			for (int j = 0; j < method.getParameterAnnotations()[i].length; j++) {
				if (method.getParameterAnnotations()[i][j] instanceof DBKey) {
					var annotatedDBKey = args[i].toString();
					if (ObjectUtils.isEmpty(annotatedDBKey)) {
						throw new IllegalArgumentException("@DBKey parameter was found. It should not be empty");
					}
					return annotatedDBKey;
				}
			}
		}

		var dbKey = mongoKeyBasedTransactional.key();

		var expr = mongoKeyBasedTransactional.expr();
		if (StringUtils.hasText(expr)) {
			var parser = new SpelExpressionParser();
			var context = new StandardEvaluationContext();
			Parameter[] parameters = method.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				context.setVariable(parameters[i].getName(), args[i]);
			}
			dbKey = parser.parseExpression(expr).getValue(context, String.class);
		}

		if (ObjectUtils.isEmpty(dbKey)) {
			throw new IllegalArgumentException("dbKey parameter is empty");
		}
		return dbKey;
	}

	protected boolean isNoRollbackError(Throwable error, Class<? extends Throwable>[] noRollbackFor) {
		return Arrays.stream(noRollbackFor).anyMatch((noRollbackClass) -> noRollbackClass.isInstance(error));
	}

	protected boolean isRollbackError(Throwable error, Class<? extends Throwable>[] rollbackFor) {
		return Arrays.stream(rollbackFor).anyMatch((rollbackClass) -> rollbackClass.isInstance(error));
	}

	protected DefaultTransactionDefinition createTransactionDefinition(
			MongoKeyBasedTransactional mongoKeyBasedTransactional) {
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setPropagationBehavior(mongoKeyBasedTransactional.propagation().value());
		definition.setIsolationLevel(mongoKeyBasedTransactional.isolation().value());
		definition.setTimeout(mongoKeyBasedTransactional.timeout());
		definition.setReadOnly(mongoKeyBasedTransactional.readOnly());
		return definition;
	}

}
