package org.jenie.spring.helloworld.test;

import org.jenie.spring.client.Constant.Protocol;
import org.jenie.spring.helloworld.HelloworldTestConfig;
import org.jenie.spring.helloworld.HelloworldTestProperties;
import org.jenie.spring.helloworld.operation.ArticleGrpcOperation;
import org.jenie.spring.helloworld.operation.ArticleGrpcReactiveOperation;
import org.jenie.spring.helloworld.operation.ArticleOperation;
import org.jenie.spring.helloworld.operation.ArticleRestOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { HelloworldTestConfig.class })
public abstract class HelloworldTests {

	@Autowired
	protected HelloworldTestProperties testProperties;

	@Autowired
	protected ArticleRestOperation articleRestOperation;

	@Autowired
	protected ArticleRestOperation articleRestReactiveOperation;

	@Autowired
	protected ArticleGrpcOperation articleGrpcOperation;

	@Autowired
	protected ArticleGrpcReactiveOperation articleGrpcReactiveOperation;

	@Autowired
	protected ArticleGrpcOperation articleGrpcArmeriaOperation;

	@Autowired
	protected ArticleGrpcReactiveOperation articleGrpcArmeriaReactiveOperation;

	protected ArticleOperation articleOperation(Protocol protocol) {
		return switch (protocol) {
			case rest -> this.articleRestOperation;
			case restReactive -> this.articleRestReactiveOperation;
			case grpc -> this.articleGrpcOperation;
			case grpcReactive -> this.articleGrpcReactiveOperation;
			case grpcArmeria -> this.articleGrpcArmeriaOperation;
			case grpcArmeriaReactive -> this.articleGrpcArmeriaReactiveOperation;
			default -> throw new IllegalArgumentException("unsupported protocol: " + protocol);
		};
	}

}
