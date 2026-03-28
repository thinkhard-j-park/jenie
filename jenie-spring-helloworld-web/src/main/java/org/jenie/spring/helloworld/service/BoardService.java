package org.jenie.spring.helloworld.service;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.jenie.spring.helloworld.annotation.ConditionalOnImperative;
import org.jenie.spring.helloworld.entity.board.BoardEntity;
import org.jenie.spring.helloworld.repository.BoardRepository;
import org.jspecify.annotations.Nullable;

import org.springframework.stereotype.Service;

@ConditionalOnImperative
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
