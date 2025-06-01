package org.jenie.spring.helloworld.test;

import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
public class ArticleDevTests extends HelloworldTests {

	@Test
	void checkProperties() {
		assertThat(this.testProperties).isNotNull();
		assertThat(this.testProperties.getClientName()).isEqualTo("helloworld-dev");
		assertThat(this.testProperties.getRestUrl()).isEqualTo("http://192.168.0.14:30000");
		assertThat(this.testProperties.getGrpcUrl()).isEqualTo("http://192.168.0.14:30005");
	}

	@Test
	void hello() {
		var message = this.helloGrpcArmeriaOperation.hello("grpc-test");
		assertThat(message).isEqualTo("Hello! grpc-test");
	}

}
