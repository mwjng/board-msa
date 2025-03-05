package practice.board.view.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ArticleViewDistributedLockRepository {

    // view::article::{article_id}::user::{user_id}::lock
    private static final String KEY_FORMAT = "view::article::%s::user::%s::lock";

    private final StringRedisTemplate redisTemplate;

    public boolean lock(Long articleId, Long userId, Duration ttl) {
        final String key = generateKey(articleId, userId);
        return Objects.requireNonNull(redisTemplate.opsForValue().setIfAbsent(key, "", ttl));
    }

    private String generateKey(Long articleId, Long userId) {
        return String.format(KEY_FORMAT, articleId, userId);
    }
}
