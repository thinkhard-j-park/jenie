package org.jenie.spring.helloworld.reactive.service;

import java.util.concurrent.ConcurrentHashMap;

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

	private final ConcurrentHashMap<String, BoardEntity> boardCache;

	private final ReactiveBoardRepository boardRepository;

	public ReactiveBoardService(ReactiveBoardRepository boardRepository) {
		this.boardRepository = boardRepository;
		this.boardCache = new ConcurrentHashMap<>();
	}

	public Mono<BoardEntity> findBoardEntityById(String service, String id) {
		var boardKey = service + "_" + id;
		if (this.boardCache.containsKey(boardKey)) {
			return Mono.just(this.boardCache.get(boardKey));
		}

		return this.boardRepository.findBoardById(service, id).map((boardEntity) -> {
			this.boardCache.putIfAbsent(boardKey, boardEntity);
			return boardEntity;
		});
	}

}
