package org.jenie.spring.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = { MongoReactiveAutoConfiguration.class, DataMongoReactiveAutoConfiguration.class })
public class JenieSpringHelloworldRestReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(JenieSpringHelloworldRestReactiveApplication.class, args);
	}

}
