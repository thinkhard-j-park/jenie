package org.jenie.spring.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = { MongoReactiveAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class })
public class JenieSpringHelloworldRestReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(JenieSpringHelloworldRestReactiveApplication.class, args);
	}

}
