package org.jenie.spring.helloworld.service.reactive;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jenie.spring.helloworld.annotation.ConditionalOnReactive;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.repository.reactive.ReactiveBoardRepository;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;

@ConditionalOnReactive
@Service
public class ReactiveBoardService {

	private static final int TTL_MINUTES = 10;

	private final LoadingCache<BoardKey, Mono<BoardEntity>> boardCache;

	public ReactiveBoardService(ReactiveBoardRepository boardRepository) {
		this.boardCache = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(TTL_MINUTES))
			.build(new BoardLoader(boardRepository));
	}

	public Mono<BoardEntity> findBoardEntityById(String service, String id) {
		return this.boardCache.get(new BoardKey(service, id));
	}

	static class BoardLoader implements CacheLoader<BoardKey, Mono<BoardEntity>> {

		private final ReactiveBoardRepository boardRepository;

		BoardLoader(ReactiveBoardRepository boardRepository) {
			this.boardRepository = boardRepository;
		}

		@Override
		public @Nullable Mono<BoardEntity> load(BoardKey boardKey) throws Exception {
			return this.boardRepository.findBoardById(boardKey.dbKey(), boardKey.id())
				.cache(Duration.ofMinutes(TTL_MINUTES));
		}

	}

	record BoardKey(String dbKey, String id) {
	}

}
