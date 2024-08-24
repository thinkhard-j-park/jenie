package org.jenie.spring.test.client;

public class JenieSpringHelloworldTests {

	protected static Operation localOperation = new Operation(HttpClient.restClient("test", "http://localhost:30000"));

}
