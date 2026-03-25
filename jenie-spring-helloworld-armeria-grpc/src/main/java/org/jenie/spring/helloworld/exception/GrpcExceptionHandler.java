package org.jenie.spring.helloworld.exception;

import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.common.annotation.Nullable;
import com.linecorp.armeria.common.grpc.GrpcExceptionHandlerFunction;
import io.grpc.Metadata;
import io.grpc.Status;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class GrpcExceptionHandler implements GrpcExceptionHandlerFunction {

	public Status handleGrpcException(AbstractException ex, Metadata metadata) {
		var errorCode = ex.getErrorCode();
		var grpcStatus = errorCode.getGrpcStatus();
		Status newStatus = grpcStatus.toStatus().withDescription(ex.getMessage()).withCause(ex);

		Metadata.Key<String> keyTitle = Metadata.Key.of("title", Metadata.ASCII_STRING_MARSHALLER);
		metadata.put(keyTitle, errorCode.getTitle());

		Metadata.Key<String> keyErrorCode = Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER);
		metadata.put(keyErrorCode, String.valueOf(errorCode.getCode()));

		return newStatus;
	}

	@Override
	public @Nullable Status apply(@NonNull RequestContext ctx, @NonNull Status status, @NonNull Throwable cause,
			@NonNull Metadata metadata) {
		if (cause instanceof AbstractException abstractException) {
			return handleGrpcException(abstractException, metadata);
		}

		return status;
	}

}
