package org.jenie.spring.helloworld.service.sync;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jenie.spring.helloworld.annotation.ConditionalOnSync;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.repository.sync.BoardRepository;

import org.springframework.stereotype.Service;

@ConditionalOnSync
@Service
public class BoardService {

	private final LoadingCache<BoardKey, BoardEntity> boardCache;

	public BoardService(BoardRepository boardRepository) {
		this.boardCache = Caffeine.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(10))
			.build(new BoardLoader(boardRepository));
	}

	public BoardEntity findBoardEntityById(String service, String id) {
		return this.boardCache.get(new BoardKey(service, id));
	}

	static class BoardLoader implements CacheLoader<BoardKey, BoardEntity> {

		private final BoardRepository boardRepository;

		BoardLoader(BoardRepository boardRepository) {
			this.boardRepository = boardRepository;
		}

		@Override
		public @Nullable BoardEntity load(BoardKey boardKey) throws Exception {
			return this.boardRepository.findBoardById(boardKey.dbKey(), boardKey.id());
		}

	}

	record BoardKey(String dbKey, String id) {
	}

}
