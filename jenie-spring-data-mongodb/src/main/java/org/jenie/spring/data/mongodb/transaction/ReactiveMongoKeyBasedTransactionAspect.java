package org.jenie.spring.data.mongodb.transaction;

import java.util.concurrent.atomic.AtomicReference;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.reactivestreams.Publisher;
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
				.flatMap((txManager) -> createTransactionalFlux(pjp, txManager, definition, mongoKeyBasedTransactional)
					.singleOrEmpty());
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

	private Flux<?> createTransactionalFlux(ProceedingJoinPoint pjp, ReactiveTransactionManager txManager,
			DefaultTransactionDefinition definition, MongoKeyBasedTransactional transactionConfig) {
		var txOp = TransactionalOperator.create(txManager, definition);
		return Flux.defer(() -> {
			// Keep the saved error local to each subscription, including retries.
			var noRollbackError = new AtomicReference<Throwable>();
			var source = Flux.defer(() -> {
				try {
					return (Publisher<?>) pjp.proceed();
				}
				catch (Throwable error) {
					return Flux.error(error);
				}
			}).onErrorResume((error) -> {
				if (isNoRollbackError(error, transactionConfig.noRollbackFor())) {
					// Complete inside the transaction so the operator commits instead of
					// rolling back.
					noRollbackError.set(error);
					return Flux.empty();
				}
				return Flux.error(error);
			});

			// Restore the original error only after a successful commit. Transaction
			// failures and cancellation remain under TransactionalOperator's control.
			return txOp.transactional(source).concatWith(Mono.defer(() -> {
				var error = noRollbackError.get();
				return (error != null) ? Mono.error(error) : Mono.empty();
			}));
		});
	}

}
