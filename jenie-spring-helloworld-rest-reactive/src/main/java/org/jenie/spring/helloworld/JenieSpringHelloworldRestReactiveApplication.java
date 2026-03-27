package org.jenie.spring.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoAutoConfiguration;
import org.springframework.boot.data.mongodb.autoconfigure.DataMongoReactiveAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoReactiveAutoConfiguration;

@SpringBootApplication(exclude = { MongoAutoConfiguration.class, DataMongoAutoConfiguration.class,
		MongoReactiveAutoConfiguration.class, DataMongoReactiveAutoConfiguration.class })
public class JenieSpringHelloworldRestReactiveApplication {

	static void main(String[] args) {
		SpringApplication.run(JenieSpringHelloworldRestReactiveApplication.class, args);
	}

}
