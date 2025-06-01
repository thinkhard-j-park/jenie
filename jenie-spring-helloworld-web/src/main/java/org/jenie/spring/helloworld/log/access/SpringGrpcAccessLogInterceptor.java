package org.jenie.spring.helloworld.log.access;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import io.grpc.ForwardingServerCall;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Grpc;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import org.jenie.spring.helloworld.utils.ZdtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringGrpcAccessLogInterceptor implements ServerInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(SpringGrpcAccessLogInterceptor.class);

	private static final Metadata.Key<String> CLIENT_IP_KEY = Metadata.Key.of("x-client-ip",
			Metadata.ASCII_STRING_MARSHALLER);

	private static final Metadata.Key<String> X_FORWARDED_FOR_KEY = Metadata.Key.of("x-forwarded-for",
			Metadata.ASCII_STRING_MARSHALLER);

	private static final Metadata.Key<String> TRACE_ID_KEY = Metadata.Key.of("x-b3-traceid",
			Metadata.ASCII_STRING_MARSHALLER);

	private static final Metadata.Key<String> SPAN_ID_KEY = Metadata.Key.of("x-b3-spanid",
			Metadata.ASCII_STRING_MARSHALLER);

	private static final Metadata.Key<String> REFERER_KEY = Metadata.Key.of("Referer",
			Metadata.ASCII_STRING_MARSHALLER);

	private static final Metadata.Key<String> USER_AGENT_KEY = Metadata.Key.of("User-Agent",
			Metadata.ASCII_STRING_MARSHALLER);

	private static final String LOG_TIMESTAMP_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";

	private static final String HEALTH_CHECK = "grpc.health.v1.Health";

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata,
			ServerCallHandler<ReqT, RespT> serverCallHandler) {
		var method = "POST";
		var clientIP = Optional.ofNullable(metadata.get(CLIENT_IP_KEY))
			.or(() -> Optional.ofNullable(metadata.get(X_FORWARDED_FOR_KEY)).map((xff) -> {
				String[] ips = xff.split(",");
				return (ips.length > 0) ? ips[0].trim() : null;
			}))
			.orElseGet(() -> {
				SocketAddress remoteAddress = serverCall.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR);
				if (remoteAddress instanceof InetSocketAddress inetSocketAddress) {
					return inetSocketAddress.getAddress().getHostAddress();
				}
				return "-";
			});
		var grpcMethod = serverCall.getMethodDescriptor().getFullMethodName();
		var httpStatusCode = 200;
		var traceId = Optional.ofNullable(metadata.get(TRACE_ID_KEY)).orElse("-");
		var spanId = Optional.ofNullable(metadata.get(SPAN_ID_KEY)).orElse("-");
		var referer = Optional.ofNullable(metadata.get(REFERER_KEY)).orElse("-");
		var userAgent = Optional.ofNullable(metadata.get(USER_AGENT_KEY)).orElse("-");
		var startTime = Instant.now();

		return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(serverCallHandler
			.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(serverCall) {
				private final AtomicLong responseMessageSize = new AtomicLong(0L);

				@Override
				protected ServerCall<ReqT, RespT> delegate() {
					return serverCall;
				}

				@Override
				public void sendMessage(RespT message) {
					if (!grpcMethod.contains(HEALTH_CHECK)
							&& message instanceof com.google.protobuf.MessageLite messageLite) {
						this.responseMessageSize.addAndGet(messageLite.getSerializedSize());
					}
					super.sendMessage(message);
				}

				@Override
				public void close(Status status, Metadata trailers) {
					Instant endTime = Instant.now();
					var duration = endTime.toEpochMilli() - startTime.toEpochMilli();
					if (!grpcMethod.contains(HEALTH_CHECK)) {
						logger.info("{} {} {} /{} {} {} {} {} {} {} {} {}", ZdtUtil.zdtNowString(LOG_TIMESTAMP_PATTERN),
								clientIP, method, grpcMethod, httpStatusCode, status.getCode().value(),
								this.responseMessageSize, duration, traceId, spanId, referer, userAgent);
					}
					super.close(status, trailers);
				}
			}, metadata)) {
			@Override
			public void onCancel() {
				super.onCancel();
			}
		};

	}

}
