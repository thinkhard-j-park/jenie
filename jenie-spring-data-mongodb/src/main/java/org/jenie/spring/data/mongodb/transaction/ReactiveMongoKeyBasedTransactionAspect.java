package org.jenie.spring.data.mongodb.transaction;

import java.util.function.Function;
import java.util.function.Supplier;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
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

	@SuppressWarnings("ReactiveStreamsUnusedPublisher")
	@Around("@annotation(mongoKeyBasedTransactional)")
	public Object around(ProceedingJoinPoint pjp, MongoKeyBasedTransactional mongoKeyBasedTransactional) {
		var dbKey = getDBKey(pjp, mongoKeyBasedTransactional);
		var definition = createTransactionDefinition(mongoKeyBasedTransactional);
		var returnType = ((MethodSignature) pjp.getSignature()).getMethod().getReturnType();
		if (Mono.class.isAssignableFrom(returnType)) {
			return this.mongoTemplateRouter.transactionManager(dbKey)
				.flatMap(
						(txManager) -> createTransactionalMono(pjp, txManager, definition, mongoKeyBasedTransactional));
		}
		else if (Flux.class.isAssignableFrom(returnType)) {
			return this.mongoTemplateRouter.transactionManager(dbKey)
				.flatMapMany(
						(txManager) -> createTransactionalFlux(pjp, txManager, definition, mongoKeyBasedTransactional));

		}
		else {
			throw new IllegalArgumentException("Unsupported return type: " + returnType);
		}

	}

	private Mono<?> createTransactionalMono(ProceedingJoinPoint pjp, ReactiveTransactionManager txManager,
			DefaultTransactionDefinition definition, MongoKeyBasedTransactional transactionConfig) {
		var txOp = TransactionalOperator.create(txManager, definition);
		try {
			return txOp.transactional((Mono<?>) pjp.proceed())
				.onErrorResume((error) -> handleTransactionError(error, transactionConfig, Mono::error, Mono::empty));
		}
		catch (Throwable err) {
			return Mono.error(new RuntimeException(err));
		}
	}

	private Flux<?> createTransactionalFlux(ProceedingJoinPoint pjp, ReactiveTransactionManager txManager,
			DefaultTransactionDefinition definition, MongoKeyBasedTransactional transactionConfig) {
		var txOp = TransactionalOperator.create(txManager, definition);
		try {
			return txOp.transactional((Flux<?>) pjp.proceed())
				.onErrorResume((error) -> handleTransactionError(error, transactionConfig, Flux::error, Flux::empty));
		}
		catch (Throwable err) {
			return Flux.error(new RuntimeException(err));
		}
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
