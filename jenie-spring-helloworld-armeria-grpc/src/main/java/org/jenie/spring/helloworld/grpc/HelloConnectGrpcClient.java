package org.jenie.spring.helloworld.grpc;

import com.linecorp.armeria.client.grpc.GrpcClients;
import com.linecorp.armeria.client.logging.LoggingClient;
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import io.grpc.stub.StreamObserver;
import org.jenie.spring.helloworld.config.HelloworldProperties;

import org.springframework.stereotype.Service;

@Service
public class HelloConnectGrpcClient {

	private final HelloServiceGrpc.HelloServiceStub client;

	public HelloConnectGrpcClient(HelloworldProperties helloworldProperties) {
		this.client = GrpcClients.builder(helloworldProperties.getConnectHelloUrl())
			.serializationFormat(GrpcSerializationFormats.PROTO)
			.decorator(LoggingClient.newDecorator())
			.build(HelloServiceGrpc.HelloServiceStub.class);
	}

	public void getConnectHello(String message, StreamObserver<HelloMessage> responseObserver) {
		var request = GetHelloRequestMessage.newBuilder().setMessage("connect: " + message).build();
		this.client.getHello(request, new StreamObserver<HelloMessage>() {
			@Override
			public void onNext(HelloMessage value) {
				responseObserver.onNext(value);
			}

			@Override
			public void onError(Throwable t) {
				responseObserver.onError(t);
			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		});
	}

}
