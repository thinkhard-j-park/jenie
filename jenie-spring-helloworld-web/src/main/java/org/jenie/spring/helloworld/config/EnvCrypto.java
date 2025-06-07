package org.jenie.spring.helloworld.config;

import java.util.HashMap;
import java.util.Map;

import org.jenie.spring.helloworld.dto.ErrorCode;
import org.jenie.spring.helloworld.exception.CommonErrors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class EnvCrypto implements BeanFactoryPostProcessor {

	private static final String PREFIX = "ENC(";

	private static final String SUFFIX = ")";

	private static final String ENV_PASSWORD = "helloworld.crypto.password";

	private static final String ENV_SALT = "helloworld.crypto.salt";

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		ConfigurableEnvironment environment = beanFactory.getBean(ConfigurableEnvironment.class);
		String password = environment.getProperty(ENV_PASSWORD);
		String salt = environment.getProperty(ENV_SALT);

		if (ObjectUtils.isEmpty(password) || ObjectUtils.isEmpty(salt)) {
			throw new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA,
					"Encryption password or salt not configured");
		}

		TextEncryptor encryptor = Encryptors.text(password, salt);
		Map<String, Object> decryptedProperties = new HashMap<>();
		for (PropertySource<?> propertySource : environment.getPropertySources()) {
			if (propertySource instanceof EnumerablePropertySource<?> enumerablePropertySource) {
				for (String propertyName : enumerablePropertySource.getPropertyNames()) {
					Object rawValue = propertySource.getProperty(propertyName);

					if (rawValue instanceof String stringValue) {
						if (stringValue.startsWith(PREFIX) && stringValue.endsWith(SUFFIX)) {
							var encrypted = stringValue.substring(PREFIX.length(),
									stringValue.length() - SUFFIX.length());
							try {
								String decryptedValue = encryptor.decrypt(encrypted);
								decryptedProperties.put(propertyName, decryptedValue);
							}
							catch (Exception ex) {
								throw new CommonErrors.IllegalDataException(ErrorCode.ILLEGAL_DATA, ex.getMessage());
							}
						}
					}
				}
			}
		}

		if (!decryptedProperties.isEmpty()) {
			var newPropertySource = new MapPropertySource("decryptedProperties", decryptedProperties);
			environment.getPropertySources().addFirst(newPropertySource);
		}

	}

}
