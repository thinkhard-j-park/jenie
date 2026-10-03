package org.jenie.spring.data.mongodb.transaction;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.jenie.spring.data.mongodb.operation.ReactiveMongoTemplateRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.TransactionSystemException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReactiveMongoKeyBasedTransactionAspectTests {

	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	@Mock
	private ReactiveMongoTemplateRouter mongoTemplateRouter;

	@Mock
	private ReactiveMongoTransactionManager transactionManager;

	@Mock
	private ReactiveTransaction transaction;

	private TransactionalService service;

	@BeforeEach
	void setUp() {
		given(this.mongoTemplateRouter.transactionManager("dbKey")).willReturn(Mono.just(this.transactionManager));
		given(this.transactionManager.getReactiveTransaction(any())).willReturn(Mono.just(this.transaction));
		var proxyFactory = new AspectJProxyFactory(new TransactionalService());
		proxyFactory.addAspect(new ReactiveMongoKeyBasedTransactionAspect(this.mongoTemplateRouter));
		this.service = proxyFactory.getProxy();
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void commitsSuccessfulTransaction(PublisherType publisherType) throws Exception {
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType, () -> Mono.just("value"));

		StepVerifier.create(result).expectNext("value").expectComplete().verify(TIMEOUT);

		verify(this.transactionManager).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void commitsEmptyTransaction(PublisherType publisherType) throws Exception {
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType, Mono::empty);

		StepVerifier.create(result).expectComplete().verify(TIMEOUT);

		verify(this.transactionManager).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void commitsNoRollbackExceptionAndPropagatesOriginalError(PublisherType publisherType) throws Exception {
		var error = new IllegalArgumentException("no rollback");
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType, () -> Mono.error(error));

		// noRollbackFor controls transaction completion; the caller must still see the
		// error.
		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);

		verify(this.transactionManager).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void rollsBackOtherExceptionsAndPropagatesOriginalError(PublisherType publisherType) throws Exception {
		var error = new IllegalStateException("rollback");
		given(this.transactionManager.rollback(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType, () -> Mono.error(error));

		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);

		verify(this.transactionManager).rollback(this.transaction);
		verify(this.transactionManager, never()).commit(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void handlesNoRollbackExceptionThrownBeforePublisherIsReturned(PublisherType publisherType) throws Exception {
		var error = new IllegalArgumentException("synchronous no rollback");
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType, () -> {
			verify(this.transactionManager).getReactiveTransaction(any());
			throw error;
		});

		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);

		verify(this.transactionManager).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void rollsBackExceptionThrownBeforePublisherIsReturned(PublisherType publisherType) throws Exception {
		var error = new IllegalStateException("synchronous rollback");
		given(this.transactionManager.rollback(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType, () -> {
			throw error;
		});

		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);

		verify(this.transactionManager).rollback(this.transaction);
		verify(this.transactionManager, never()).commit(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void propagatesNoRollbackExceptionOnlyAfterCommitCompletes(PublisherType publisherType) throws Exception {
		var error = new IllegalArgumentException("no rollback");
		var commitCompletion = Sinks.<Void>one();
		var observedError = new AtomicReference<Throwable>();
		given(this.transactionManager.commit(this.transaction)).willReturn(commitCompletion.asMono());
		var result = Flux.from(invoke(publisherType, () -> Mono.error(error))).doOnError(observedError::set);

		StepVerifier.create(result).then(() -> {
			verify(this.transactionManager).commit(this.transaction);
			assertThat(observedError.get()).isNull();
			assertThat(commitCompletion.tryEmitEmpty()).isEqualTo(Sinks.EmitResult.OK);
		}).expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error)).verify(TIMEOUT);

		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void propagatesCommitFailureInsteadOfSavedApplicationError(PublisherType publisherType) throws Exception {
		var applicationError = new IllegalArgumentException("no rollback");
		var commitError = new TransactionSystemException("commit failed");
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.error(commitError));
		var result = invoke(publisherType, () -> Mono.error(applicationError));

		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(commitError))
			.verify(TIMEOUT);

		verify(this.transactionManager).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void doesNotApplyNoRollbackRuleToTransactionStartFailure(PublisherType publisherType) throws Exception {
		var error = new IllegalArgumentException("transaction start failed");
		var invocations = new AtomicInteger();
		given(this.transactionManager.getReactiveTransaction(any())).willReturn(Mono.error(error));
		var result = invoke(publisherType, () -> {
			invocations.incrementAndGet();
			return Mono.just("value");
		});

		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);

		assertThat(invocations.get()).isZero();
		verify(this.transactionManager, never()).commit(any());
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void keepsSavedErrorsIsolatedBetweenSubscriptions(PublisherType publisherType) throws Exception {
		var error = new IllegalArgumentException("first subscription only");
		var invocations = new AtomicInteger();
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType,
				() -> (invocations.getAndIncrement() == 0) ? Mono.error(error) : Mono.empty());

		assertThat(invocations.get()).isZero();
		StepVerifier.create(result)
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);
		StepVerifier.create(result).expectComplete().verify(TIMEOUT);

		assertThat(invocations.get()).isEqualTo(2);
		verify(this.transactionManager, times(2)).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	@ParameterizedTest
	@EnumSource(PublisherType.class)
	void rollsBackOnCancellation(PublisherType publisherType) throws Exception {
		var subscribed = new AtomicBoolean();
		given(this.transactionManager.rollback(this.transaction)).willReturn(Mono.empty());
		var result = invoke(publisherType,
				() -> Mono.<String>never().doOnSubscribe((subscription) -> subscribed.set(true)));

		StepVerifier.create(result).then(() -> assertThat(subscribed.get()).isTrue()).thenCancel().verify(TIMEOUT);

		verify(this.transactionManager).rollback(this.transaction);
		verify(this.transactionManager, never()).commit(any());
	}

	@Test
	void preservesFluxValuesBeforeNoRollbackException() throws Exception {
		var error = new IllegalArgumentException("no rollback");
		given(this.transactionManager.commit(this.transaction)).willReturn(Mono.empty());
		var result = this.service.flux(() -> Flux.just("first", "second").concatWith(Mono.error(error)));

		StepVerifier.create(result)
			.expectNext("first", "second")
			.expectErrorSatisfies((actual) -> assertThat(actual).isSameAs(error))
			.verify(TIMEOUT);

		verify(this.transactionManager).commit(this.transaction);
		verify(this.transactionManager, never()).rollback(any());
	}

	private Publisher<String> invoke(PublisherType publisherType, Callable<Publisher<String>> action) throws Exception {
		return switch (publisherType) {
			case MONO -> this.service.mono(() -> Mono.from(action.call()));
			case FLUX -> this.service.flux(() -> Flux.from(action.call()));
		};
	}

	enum PublisherType {

		MONO, FLUX

	}

	static class TransactionalService {

		@MongoKeyBasedTransactional(key = "dbKey", noRollbackFor = IllegalArgumentException.class)
		Mono<String> mono(Callable<Mono<String>> action) throws Exception {
			return action.call();
		}

		@MongoKeyBasedTransactional(key = "dbKey", noRollbackFor = IllegalArgumentException.class)
		Flux<String> flux(Callable<Flux<String>> action) throws Exception {
			return action.call();
		}

	}

}
