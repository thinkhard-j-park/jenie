package org.jenie.spring.helloworld.operation;

import org.jenie.spring.helloworld.grpc.GetHelloRequestMessage;
import org.jenie.spring.helloworld.grpc.HelloServiceGrpc;

public class HelloGrpcOperation {

	private final HelloServiceGrpc.HelloServiceBlockingStub stub;

	public HelloGrpcOperation(HelloServiceGrpc.HelloServiceBlockingStub stub) {
		this.stub = stub;
	}

	public String hello(String message) {
		var requestMessage = GetHelloRequestMessage.newBuilder().setMessage(message).build();
		return this.stub.getHello(requestMessage).getMessage();
	}

}
