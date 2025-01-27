package org.jenie.spring.client;

import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.Status;
import org.jenie.spring.helloworld.HelloworldTestProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogGrpcInterceptor implements ClientInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(LogGrpcInterceptor.class);

	private final HelloworldTestProperties properties;

	public LogGrpcInterceptor(HelloworldTestProperties properties) {
		this.properties = properties;
	}

	@Override
	public <ReqT, RespT> io.grpc.ClientCall<ReqT, RespT> interceptCall(io.grpc.MethodDescriptor<ReqT, RespT> method,
			io.grpc.CallOptions callOptions, io.grpc.Channel next) {

		ClientCall<ReqT, RespT> originalCall = next.newCall(method, callOptions);
		return new ForwardingClientCall.SimpleForwardingClientCall<>(originalCall) {
			@Override
			public void sendMessage(ReqT message) {
				logger.info("[{}] Request -> {}\n{}", LogGrpcInterceptor.this.properties.getClientName(),
						method.getFullMethodName(), message);
				super.sendMessage(message);
			}

			@Override
			public void start(Listener<RespT> responseListener, Metadata headers) {
				Listener<RespT> loggingListener = new ForwardingClientCallListener.SimpleForwardingClientCallListener<>(
						responseListener) {

					@Override
					public void onHeaders(Metadata headers) {
						logger.info("[{}] Headers <- {}\n{}", LogGrpcInterceptor.this.properties.getClientName(),
								method.getFullMethodName(), headers);
						super.onHeaders(headers);
					}

					@Override
					public void onMessage(RespT message) {
						logger.info("[{}] Message <- {}\n{}", LogGrpcInterceptor.this.properties.getClientName(),
								method.getFullMethodName(), message);
						super.onMessage(message);
					}

					@Override
					public void onClose(Status status, Metadata trailers) {
						logger.info("[{}] Close - {}, {}, {}", LogGrpcInterceptor.this.properties.getClientName(),
								method.getFullMethodName(), status, trailers);
						super.onClose(status, trailers);
					}
				};
				super.start(loggingListener, headers);
			}
		};
	}

}
