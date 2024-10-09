package org.jenie.spring.helloworld.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WriterTests {

	@Test
	void checkWriter() {
		var writer = new Writer();
		assertThat(writer).isNotNull();
	}

}
