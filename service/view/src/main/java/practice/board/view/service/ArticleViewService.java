package practice.board.view.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practice.board.view.repository.ArticleViewCountBackUpRepository;
import practice.board.view.repository.ArticleViewCountRepository;
import practice.board.view.repository.ArticleViewDistributedLockRepository;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ArticleViewService {

    private static final int BACK_UP_BATCH_SIZE = 100;
    public static final Duration TTL = Duration.ofMinutes(10);

    private final ArticleViewCountBackUpProcessor articleViewCountBackUpProcessor;
    private final ArticleViewCountRepository articleViewCountRepository;
    private final ArticleViewDistributedLockRepository articleViewDistributedLockRepository;

    public Long increase(Long articleId, Long userId) {
        if (!articleViewDistributedLockRepository.lock(articleId, userId, TTL)) {
            return articleViewCountRepository.read(articleId);
        }

        final Long count = articleViewCountRepository.increase(articleId);
        if (count % BACK_UP_BATCH_SIZE == 0) {
            articleViewCountBackUpProcessor.backUp(articleId, count);
        }
        return count;
    }

    public Long count(Long articleId) {
        return articleViewCountRepository.read(articleId);
    }
}
