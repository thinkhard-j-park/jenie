package org.jenie.spring.helloworld.grpc;

import io.grpc.stub.StreamObserver;

import org.springframework.stereotype.Service;

@Service
public class HelloGrpc extends HelloServiceGrpc.HelloServiceImplBase {

	@Override
	public void getHello(GetHelloRequestMessage request, StreamObserver<HelloMessage> responseObserver) {
		var message = HelloMessage.newBuilder().setMessage("Hello! " + request.getMessage()).build();
		responseObserver.onNext(message);
		responseObserver.onCompleted();
	}

}
