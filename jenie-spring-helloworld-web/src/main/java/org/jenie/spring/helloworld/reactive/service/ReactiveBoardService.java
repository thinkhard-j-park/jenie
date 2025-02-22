package org.jenie.spring.helloworld.reactive.service;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.reactive.repository.ReactiveBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@ConditionalOnReactive
@Service
public class ReactiveBoardService {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveBoardService.class);

	private static final int TTL_MINUTES = 10;

	private final Cache<BoardKey, BoardEntity> boardCache;

	private final ReactiveBoardRepository boardRepository;

	public ReactiveBoardService(ReactiveBoardRepository boardRepository) {
		this.boardRepository = boardRepository;
		this.boardCache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(TTL_MINUTES)).build();
	}

	public Mono<BoardEntity> findBoardEntityById(String service, String id) {
		var boardKey = new BoardKey(service, id);
		var result = this.boardCache.getIfPresent(boardKey);
		if (result != null) {
			return Mono.just(result);
		}

		return this.boardRepository.findBoardById(service, id).map((boardEntity) -> {
			this.boardCache.put(boardKey, boardEntity);
			return boardEntity;
		});
	}

	record BoardKey(String dbKey, String id) {
	}

}
