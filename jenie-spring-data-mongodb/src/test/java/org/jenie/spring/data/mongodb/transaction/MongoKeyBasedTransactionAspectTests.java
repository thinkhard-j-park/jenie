package org.jenie.spring.data.mongodb.transaction;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jenie.spring.data.mongodb.operation.MongoTemplateRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.TransactionStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MongoKeyBasedTransactionAspectTests {

	@InjectMocks
	private MongoKeyBasedTransactionAspect mongoKeyBasedTransactionAspect;

	@Mock
	private ProceedingJoinPoint pjp;

	@Mock
	private MethodSignature methodSignature;

	@Mock
	private MongoTemplateRouter mongoTemplateRouter;

	@Mock
	private MongoTransactionManager transactionManager;

	@Mock
	private TransactionStatus transactionStatus;

	@Test
	void getDbKeyWithArgsAnnotation1() throws NoSuchMethodException {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod1", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(new Object[] { dbKey, "someValue" });

		// when
		var result = this.mongoKeyBasedTransactionAspect.getDBKey(this.pjp, methodAnnotation);

		// then
		assertThat(result).isEqualTo(dbKey);
	}

	@Test
	void getDbKeyWithArgsAnnotation2() throws NoSuchMethodException {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod2", String.class, String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(new Object[] { "arg", dbKey, "someValue" });

		// when
		var result = this.mongoKeyBasedTransactionAspect.getDBKey(this.pjp, methodAnnotation);

		// then
		assertThat(result).isEqualTo(dbKey);
	}

	@Test
	void getDbKeyWithArgsAnnotationShouldFail() throws NoSuchMethodException {
		// given
		var dbKey = "";
		var method = TestClass.class.getDeclaredMethod("txMethod1", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(new Object[] { dbKey, "someValue" });

		// when, then
		assertThatThrownBy(() -> this.mongoKeyBasedTransactionAspect.getDBKey(this.pjp, methodAnnotation))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void getDbKeyWithMethodAnnotation() throws NoSuchMethodException {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod3", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(new Object[] { dbKey, "someValue" });

		// when
		var result = this.mongoKeyBasedTransactionAspect.getDBKey(this.pjp, methodAnnotation);

		// then
		assertThat(result).isEqualTo(dbKey);
	}

	@Test
	void getDbKeyShouldFail() throws NoSuchMethodException {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod4", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(new Object[] { dbKey, "someValue" });

		// when, then
		assertThatThrownBy(() -> this.mongoKeyBasedTransactionAspect.getDBKey(this.pjp, methodAnnotation))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void aroundSuccessfulTransaction() throws Throwable {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod1", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);
		var args = new Object[] { dbKey, "someValue" };

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(args);
		given(this.mongoTemplateRouter.transactionManager(any())).willReturn(this.transactionManager);
		given(this.transactionManager.getTransaction(any())).willReturn(this.transactionStatus);
		given(this.pjp.proceed()).willReturn(method.invoke(new TestClass(), args));

		// when
		var result = this.mongoKeyBasedTransactionAspect.around(this.pjp, methodAnnotation);

		// then
		verify(this.transactionManager).commit(this.transactionStatus);
		verify(this.transactionManager, never()).rollback(this.transactionStatus);
		assertThat(result).isEqualTo("success");
	}

	@Test
	void aroundRollbackTransaction() throws Throwable {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod5", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);
		var args = new Object[] { dbKey, "someValue" };

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(args);
		given(this.mongoTemplateRouter.transactionManager(any())).willReturn(this.transactionManager);
		given(this.transactionManager.getTransaction(any())).willReturn(this.transactionStatus);
		given(this.pjp.proceed()).willThrow(new RuntimeException());

		// when
		assertThatThrownBy(() -> this.mongoKeyBasedTransactionAspect.around(this.pjp, methodAnnotation))
			.isInstanceOf(RuntimeException.class);

		// then
		verify(this.transactionManager, never()).commit(this.transactionStatus);
		verify(this.transactionManager).rollback(this.transactionStatus);
	}

	@Test
	void aroundTransactionWithNoRollbackException() throws Throwable {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod6", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);
		var args = new Object[] { dbKey, "someValue" };

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(args);
		given(this.mongoTemplateRouter.transactionManager(any())).willReturn(this.transactionManager);
		given(this.transactionManager.getTransaction(any())).willReturn(this.transactionStatus);
		given(this.pjp.proceed()).willThrow(new IllegalArgumentException());

		// when
		assertThatThrownBy(() -> this.mongoKeyBasedTransactionAspect.around(this.pjp, methodAnnotation))
			.isInstanceOf(IllegalArgumentException.class);

		// then
		verify(this.transactionManager).commit(this.transactionStatus);
		verify(this.transactionManager, never()).rollback(this.transactionStatus);
	}

	@Test
	void aroundTransactionWithRollbackException() throws Throwable {
		// given
		var dbKey = "dbKey";
		var method = TestClass.class.getDeclaredMethod("txMethod7", String.class, String.class);
		var methodAnnotation = method.getAnnotation(MongoKeyBasedTransactional.class);
		var args = new Object[] { dbKey, "someValue" };

		given(this.pjp.getSignature()).willReturn(this.methodSignature);
		given(this.methodSignature.getMethod()).willReturn(method);
		given(this.pjp.getArgs()).willReturn(args);
		given(this.mongoTemplateRouter.transactionManager(any())).willReturn(this.transactionManager);
		given(this.transactionManager.getTransaction(any())).willReturn(this.transactionStatus);
		given(this.pjp.proceed()).willThrow(new RuntimeException());

		// when
		assertThatThrownBy(() -> this.mongoKeyBasedTransactionAspect.around(this.pjp, methodAnnotation))
			.isInstanceOf(RuntimeException.class);

		// then
		verify(this.transactionManager, never()).commit(this.transactionStatus);
		verify(this.transactionManager).rollback(this.transactionStatus);
	}

	static class TestClass {

		@MongoKeyBasedTransactional
		String txMethod1(@DBKey String someKey, String someValue) {
			return "success";
		}

		@MongoKeyBasedTransactional
		String txMethod2(String arg, @DBKey String someKey, String someValue) {
			return "success";
		}

		@MongoKeyBasedTransactional(key = "dbKey")
		String txMethod3(String someKey, String someValue) {
			return "success";
		}

		@MongoKeyBasedTransactional
		void txMethod4(String someKey, String someValue) {
		}

		@MongoKeyBasedTransactional
		void txMethod5(@DBKey String someKey, String someValue) {
			throw new RuntimeException();
		}

		@MongoKeyBasedTransactional(noRollbackFor = { IllegalArgumentException.class })
		String txMethod6(@DBKey String someKey, String someValue) {
			return "success";
		}

		@MongoKeyBasedTransactional(rollbackFor = { RuntimeException.class })
		String txMethod7(@DBKey String someKey, String someValue) {
			throw new RuntimeException();
		}

	}

}
