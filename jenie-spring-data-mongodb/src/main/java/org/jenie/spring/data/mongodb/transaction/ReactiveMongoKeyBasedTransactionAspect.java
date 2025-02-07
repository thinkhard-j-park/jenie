package org.jenie.spring.data.mongodb.transaction;

import java.util.function.Function;
import java.util.function.Supplier;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Aspect
public class ReactiveMongoKeyBasedTransactionAspect extends AbstractMongoKeyBasedTransactionalAspect {

	private final ReactiveMongoTemplateRouter mongoTemplateRouter;

	public ReactiveMongoKeyBasedTransactionAspect(ReactiveMongoTemplateRouter mongoTemplateRouter) {
		this.mongoTemplateRouter = mongoTemplateRouter;
	}

	@Around("@annotation(mongoKeyBasedTransactional)")
	public Object around(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional) {
		var dbKey = getDBKey(pjp, mongoKeyBasedTransactional);
		var definition = createTransactionDefinition(mongoKeyBasedTransactional);

		return this.mongoTemplateRouter.transactionManager(dbKey)
			.mapNotNull((txManager) -> handleTransaction(pjp, mongoKeyBasedTransactional, txManager, definition));
	}

	private Object handleTransaction(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional,
			ReactiveTransactionManager reactiveTxnManager, DefaultTransactionDefinition definition) {
		try {
			var result = pjp.proceed();

			if (result instanceof Mono<?> mono) {
				return createTransactionalMono(mono, reactiveTxnManager, definition, mongoKeyBasedTransactional);
			}
			else if (result instanceof Flux<?> flux) {
				return createTransactionalFlux(flux, reactiveTxnManager, definition, mongoKeyBasedTransactional);
			}

			throw new IllegalStateException(
					"Unsupported return type for ReactiveMongoKeyBasedTransactional: " + result.getClass());
		}
		catch (Throwable err) {
			throw new RuntimeException("Transaction processing failed", err);
		}
	}

	private Mono<?> createTransactionalMono(Mono<?> mono, ReactiveTransactionManager reactiveTxnManager,
			DefaultTransactionDefinition definition, MongoKeyBasedTransactional transactionConfig) {
		//TODO 이런식으로 처리 하면 동작하지 않음...
		var txOp = TransactionalOperator.create(reactiveTxnManager, definition);
		return txOp.transactional(mono)
			.onErrorResume((error) -> handleTransactionError(error, transactionConfig, Mono::error, Mono::empty));
	}

	private Flux<?> createTransactionalFlux(Flux<?> flux, ReactiveTransactionManager reactiveTxnManager,
			DefaultTransactionDefinition definition, MongoKeyBasedTransactional transactionConfig) {
		var txOp = TransactionalOperator.create(reactiveTxnManager, definition);
		return txOp.transactional(flux)
			.onErrorResume((error) -> handleTransactionError(error, transactionConfig, Flux::error, Flux::empty));
	}

	private <T> T handleTransactionError(Throwable error, MongoKeyBasedTransactional transactionConfig,
			Function<Throwable, T> errorHandler, Supplier<T> emptyHandler) {
		if (isNoRollbackError(error, transactionConfig.noRollbackFor())) {
			return emptyHandler.get();
		}

		if (isRollbackError(error, transactionConfig.rollbackFor())) {
			return errorHandler.apply(error);
		}

		return errorHandler.apply(error);
	}

}
