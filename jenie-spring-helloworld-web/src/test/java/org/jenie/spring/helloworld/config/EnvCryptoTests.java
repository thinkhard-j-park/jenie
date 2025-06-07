package org.jenie.spring.helloworld.config;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.keygen.KeyGenerators;

import static org.assertj.core.api.Assertions.assertThat;

public class EnvCryptoTests {

	private static final Logger logger = LoggerFactory.getLogger(EnvCryptoTests.class);

	@Test
	void encryptDecrypt() {
		var plain = "localhost:27017";
		var salt = KeyGenerators.string().generateKey();
		var masterPass = "jenie";

		var encryptor = Encryptors.text(masterPass, salt);
		var encrypted = encryptor.encrypt(plain);
		logger.info("{}, encrypted: {}", salt, encrypted);
		assertThat(encrypted).isNotEmpty();

		var decrypted = encryptor.decrypt(encrypted);
		assertThat(decrypted).isEqualTo(plain);
		logger.info("decrypted: {}", decrypted);
	}

}
