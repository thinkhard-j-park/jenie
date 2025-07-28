package org.jenie.spring.helloworld.reactive.service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.github.benmanes.caffeine.cache.AsyncCacheLoader;
import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.reactive.repository.ReactiveBoardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@ConditionalOnReactive
@Service
public class ReactiveBoardService {

	private static final Logger logger = LoggerFactory.getLogger(ReactiveBoardService.class);

	private static final int TTL_MINUTES = 10;

	private final AsyncLoadingCache<BoardKey, BoardEntity> boardCache;

	public ReactiveBoardService(ReactiveBoardRepository boardRepository) {
		this.boardCache = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(TTL_MINUTES))
			.buildAsync(new BoardLoader(boardRepository));
	}

	public Mono<BoardEntity> findBoardEntityById(String service, String id) {
		return Mono.fromFuture(this.boardCache.get(new BoardKey(service, id)));
	}

	static class BoardLoader implements AsyncCacheLoader<BoardKey, BoardEntity> {

		private final ReactiveBoardRepository boardRepository;

		BoardLoader(ReactiveBoardRepository boardRepository) {
			this.boardRepository = boardRepository;
		}

		@Override
		public @NonNull CompletableFuture<? extends BoardEntity> asyncLoad(@NonNull BoardKey boardKey,
				@NonNull Executor executor) {
			return this.boardRepository.findBoardById(boardKey.dbKey(), boardKey.id()).toFuture();
		}

	}

	record BoardKey(String dbKey, String id) {
	}

}
