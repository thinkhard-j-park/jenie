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
		assertThat(this.testProperties.getBaseUrl()).isEqualTo("http://dev-api:30000");
	}

}
