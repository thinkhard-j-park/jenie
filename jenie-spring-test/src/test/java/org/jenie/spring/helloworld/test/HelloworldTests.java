package org.jenie.spring.helloworld.test;

import org.jenie.spring.client.Constant.Protocol;
import org.jenie.spring.helloworld.HelloworldTestConfig;
import org.jenie.spring.helloworld.HelloworldTestProperties;
import org.jenie.spring.helloworld.operation.ArticleGrpcOperation;
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

	protected ArticleOperation articleOperation(Protocol protocol) {
		return switch (protocol) {
			case rest -> this.articleRestOperation;
			case restReactive -> this.articleRestReactiveOperation;
			case grpc -> this.articleGrpcOperation;
			default -> throw new IllegalArgumentException("unsupported protocol: " + protocol);
		};
	}

}
