package org.jenie.spring.helloworld.log.access;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

import reactor.netty.http.server.ConnectionInformation;
import reactor.netty.http.server.logging.AccessLog;

import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;

public interface ReactorNettyAccessLogCustomizer {

	default NettyServerCustomizer accessLogCustomizer() {
		//@formatter:off
		return (httpServer) -> httpServer.accessLog(true,
				(accessLogArgProvider) -> AccessLog.create("{} {} {} {} {} {} {} {} {} {} {} {} {}",
						Objects.requireNonNull(accessLogArgProvider.accessDateTime()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")),
						Optional.ofNullable(accessLogArgProvider.connectionInformation())
							.map(ConnectionInformation::connectionRemoteAddress)
							.map(Object::toString)
							.orElse("-"),
						accessLogArgProvider.method(),
						accessLogArgProvider.uri(),
						accessLogArgProvider.protocol(),
						accessLogArgProvider.status(),
						Optional.ofNullable(accessLogArgProvider.responseHeader("grpc-status")).orElse("-"),
						accessLogArgProvider.contentLength(),
						accessLogArgProvider.duration(),
						Optional.ofNullable(accessLogArgProvider.requestHeader("x-b3-traceid")).orElse("-"),
						Optional.ofNullable(accessLogArgProvider.requestHeader("x-b3-spanid")).orElse("-"),
						Optional.ofNullable(accessLogArgProvider.requestHeader("Referer")).orElse("-"),
						Optional.ofNullable(accessLogArgProvider.requestHeader("User-Agent")).orElse("-")
				));
		//@formatter:on
	}

}
