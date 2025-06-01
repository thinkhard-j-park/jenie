package org.jenie.spring.helloworld.exception;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusException;

import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class GlobalGrpcExceptionHandler implements GrpcExceptionHandler {

	@Override
	public StatusException handleException(Throwable exception) {

		if (exception instanceof AbstractException ex) {
			var errorCode = ex.getErrorCode();
			var grpcStatus = errorCode.getGrpcStatus();
			Status status = grpcStatus.toStatus().withDescription(ex.getMessage()).withCause(ex);
			var metaData = new Metadata();

			Metadata.Key<String> keyTitle = Metadata.Key.of("title", Metadata.ASCII_STRING_MARSHALLER);
			metaData.put(keyTitle, errorCode.getTitle());

			Metadata.Key<String> keyErrorCode = Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER);
			metaData.put(keyErrorCode, String.valueOf(errorCode.getCode()));

			return status.asException(metaData);
		}

		return new StatusException(Status.INTERNAL.withDescription(exception.getMessage()).withCause(exception));
	}

}
