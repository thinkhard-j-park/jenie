package org.jenie.spring.helloworld.test;

import org.jenie.spring.helloworld.HelloworldTestConfig;
import org.jenie.spring.helloworld.HelloworldTestProperties;
import org.jenie.spring.helloworld.operation.ArticleOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { HelloworldTestConfig.class })
abstract class HelloworldTests {

	@Autowired
	protected HelloworldTestProperties testProperties;

	@Autowired
	protected ArticleOperation articleOperation;

}
