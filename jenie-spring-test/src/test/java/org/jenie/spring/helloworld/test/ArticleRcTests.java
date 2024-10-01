package org.jenie.spring.helloworld.test;

import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("rc")
public class ArticleRcTests extends HelloworldTests {

	@Test
	void checkProperties() {
		assertThat(this.testProperties).isNotNull();
		assertThat(this.testProperties.getClientName()).isEqualTo("helloworld-rc");
		assertThat(this.testProperties.getBaseUrl()).isEqualTo("http://rc-api:30000");
	}
}
