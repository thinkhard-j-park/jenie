package org.jenie.spring.helloworld.config;

import com.linecorp.armeria.server.logging.AccessLogWriter;

public interface ArmeriaServerBuilder {

	default String accessLogFormat() {
		return "%h %r %s %b %{x-b3-traceid}i %{x-b3-spanid}i";
	}

	default AccessLogWriter accessLogWriter() {
		return (requestLog) -> {
			if (requestLog.context().path().startsWith("/grpc.health.v1.Health/")) {
				return;
			}
			AccessLogWriter.custom(accessLogFormat()).log(requestLog);
		};
	}

}
