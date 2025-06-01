package org.jenie.spring.helloworld.log.access;

import com.linecorp.armeria.server.logging.AccessLogWriter;

public interface ArmeriaAccessLogCustomizer {

	default String accessLogFormat() {
		return "%{ISO_OFFSET_DATE_TIME}t %h %r %s %{grpc-status}o %b %{totalDurationMillis}L %{x-b3-traceid}i %{x-b3-spanid}i %{Referer}i %{User-Agent}i";
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
