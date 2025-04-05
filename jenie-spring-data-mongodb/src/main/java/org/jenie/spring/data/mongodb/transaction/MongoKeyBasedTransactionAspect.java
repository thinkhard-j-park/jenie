package org.jenie.spring.data.mongodb.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

@Aspect
public class MongoKeyBasedTransactionAspect extends AbstractMongoKeyBasedTransactionalAspect {

	private final MongoTemplateRouter mongoTemplateRouter;

	public MongoKeyBasedTransactionAspect(MongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

	@Around("@annotation(mongoKeyBasedTransactional)")
	public Object around(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional)
			throws Throwable {
		var dbKey = getDBKey(pjp, mongoKeyBasedTransactional);
		var definition = createTransactionDefinition(mongoKeyBasedTransactional);

		PlatformTransactionManager tm = this.mongoTemplateRouter.transactionManager(dbKey);
		TransactionStatus status = tm.getTransaction(definition);

		try {
			Object result = pjp.proceed();
			tm.commit(status);
			return result;
		}
		catch (Throwable ex) {
			boolean shouldRollback = !isNoRollbackError(ex, mongoKeyBasedTransactional.noRollbackFor());

			if (isRollbackError(ex, mongoKeyBasedTransactional.rollbackFor())) {
				shouldRollback = true;
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
