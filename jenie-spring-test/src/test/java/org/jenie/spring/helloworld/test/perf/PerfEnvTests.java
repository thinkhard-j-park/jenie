package org.jenie.spring.helloworld.test.perf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.bson.types.ObjectId;
import org.jenie.spring.data.mongodb.config.MongoDBSetting;
import org.jenie.spring.helloworld.HelloworldPerfConfig;
import org.jenie.spring.helloworld.common.ActionDateTime;
import org.jenie.spring.helloworld.common.ArticleState;
import org.jenie.spring.helloworld.common.Writer;
import org.jenie.spring.helloworld.dto.article.Article;
import org.jenie.spring.helloworld.dto.article.ArticleHeader;
import org.jenie.spring.helloworld.dto.board.Board;
import org.jenie.spring.helloworld.repository.ArticleRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Code to generate test data for performance testing. Activate and use when necessary.
 */
@Disabled
@SpringBootTest(classes = { HelloworldPerfConfig.class })
@ActiveProfiles("perf")
public class PerfEnvTests {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PerfEnvTests.class);

	@Autowired
	private MongoDBSetting mongoDBSetting;

	@Autowired
	private ArticleRepository articleRepository;

	private static final String BOARD_ID_HELLO_FREE = "67398df58a777df592683d2c";

	private static final String BOARD_ID_OLLEH_FREE = "67398e0b8a777df592683d2d";

	@Test
	void checkDataMongodb() {
		assertThat(this.mongoDBSetting).isNotNull();
		assertThat(this.articleRepository).isNotNull();
	}

	private List<String[]> readArticles() throws IOException {
		var articles = new ArrayList<String[]>();
		for (int i = 0; i < 10; i++) {
			var fileName = "article" + i + ".txt";
			var lines = Files.readAllLines(Paths.get("perf/" + fileName));
			var sb = new StringBuilder();
			for (int j = 1; j < lines.size(); j++) {
				sb.append(lines.get(j)).append("\r\n");
			}

			var title = lines.getFirst();
			var content = sb.toString();
			var titleAndContent = new String[2];
			titleAndContent[0] = title;
			titleAndContent[1] = content;
			articles.add(titleAndContent);
		}
		return articles;
	}

	static Stream<Arguments> provideDBKeyAndBoardId() {
		return Stream.of(Arguments.of("jenie-olleh", BOARD_ID_OLLEH_FREE),
				Arguments.of("jenie-hello", BOARD_ID_HELLO_FREE));
	}

	@Disabled
	@ParameterizedTest
	@MethodSource("provideDBKeyAndBoardId")
	void bulkInsert_1_000_000_records(String dbKey, String boardId) throws IOException, InterruptedException {
		var titleAndContentList = readArticles();
		for (int j = 0; j < 100; j++) {
			// 10,000
			for (int i = 0; i < 1_000; i++) {
				Collections.shuffle(titleAndContentList);
				var articleList = new ArrayList<Article>();
				for (String[] titleAndContent : titleAndContentList) {
					var title = titleAndContent[0];
					var content = titleAndContent[1];
					var objectId = new ObjectId();
					var articleHeader = ArticleHeader.newBuilder()
						.id(objectId.toString())
						.board(Board.newBuilder().id(boardId).build())
						.state(ArticleState.Normal)
						.writer(new Writer("perf-tester", "perf-tester"))
						.title(i + " " + title)
						.actionDateTime(new ActionDateTime())
						.build();

					var article = new Article(articleHeader, content);
					articleList.add(article);
				}

				this.articleRepository.bulkWriteArticle(dbKey, articleList);
			}
			Thread.sleep(1000);
			logger.info("{} records inserted", (j + 1) * 10_000);
		}
	}

	static Stream<Arguments> provideDBKeyAndCount() {
		return Stream.of(Arguments.of("jenie-olleh", 1000), Arguments.of("jenie-hello", 1000));
	}

	@Disabled
	@ParameterizedTest
	@MethodSource("provideDBKeyAndCount")
	void getRandomIds(String dbKey, int count) throws IOException {
		var filePath = "perf/" + dbKey + "-ids.txt";
		List<String> ids = this.articleRepository.getRandomIds(dbKey, count);
		Files.write(Path.of(filePath), ids);
		logger.info("{}: {} records were written.", filePath, count);
	}

}
