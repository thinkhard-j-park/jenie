package org.jenie.spring.helloworld.util;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest
@ActiveProfiles("local")
class JasyptTests {

	private static final Logger logger = LoggerFactory.getLogger(JasyptTests.class);

	@Autowired
	StringEncryptor encryptor;

	@Test
	void encryptAndDecrypt() {
		var plain = "jenie-spring-helloworld";
		var enc = this.encryptor.encrypt(plain);
		logger.info("encrypt: {}", enc);
		var dec = this.encryptor.decrypt(enc);
		logger.info("decrypt: {}", dec);
		assertThat(dec).isEqualTo(plain);
	}

}
