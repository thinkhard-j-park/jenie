package org.jenie.spring.data.mongodb.transaction;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.ObjectUtils;

@Aspect
@Component
public class MongoKeyBasedTransactionAspect {

	private final MongoTemplateRouter mongoTemplateRouter;

	public MongoKeyBasedTransactionAspect(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

	private String getDBKey(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional) {
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
		if (ObjectUtils.isEmpty(dbKey)) {
			throw new IllegalArgumentException("dbKey parameter is empty");
		}
		return dbKey;
	}

	@Around("@annotation(mongoKeyBasedTransactional)")
	public Object around(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional)
			throws Throwable {
		var dbKey = getDBKey(pjp, mongoKeyBasedTransactional);
		PlatformTransactionManager tm = this.mongoTemplateRouter.transactionManager(dbKey);

		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setPropagationBehavior(mongoKeyBasedTransactional.propagation().value());
		definition.setIsolationLevel(mongoKeyBasedTransactional.isolation().value());
		definition.setTimeout(mongoKeyBasedTransactional.timeout());
		definition.setReadOnly(mongoKeyBasedTransactional.readOnly());

		TransactionStatus status = tm.getTransaction(definition);

		try {
			Object result = pjp.proceed();
			tm.commit(status);
			return result;
		}
		catch (Throwable ex) {
			boolean shouldRollback = true;

			for (Class<? extends Throwable> noRollbackForClass : mongoKeyBasedTransactional.noRollbackFor()) {
				if (noRollbackForClass.isInstance(ex)) {
					shouldRollback = false;
					break;
				}
			}

			if (shouldRollback) {
				for (Class<? extends Throwable> rollbackForClass : mongoKeyBasedTransactional.rollbackFor()) {
					if (rollbackForClass.isInstance(ex)) {
						tm.rollback(status);
						throw ex;
					}
				}
			}

			if (shouldRollback) {
				tm.rollback(status);
			}
			else {
				tm.commit(status);
			}

			throw ex;
		}
	}

}
