package practice.board.view.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import practice.board.view.entity.ArticleViewCount;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleViewCountBackUpRepositoryTest {

    @Autowired
    ArticleViewCountBackUpRepository articleViewCountBackUpRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    @Transactional
    void updateViewCountTest() {
        // given
        articleViewCountBackUpRepository.save(
            ArticleViewCount.init(1L, 0L)
        );
        em.flush();
        em.clear();

        final int result1 = articleViewCountBackUpRepository.updateViewCount(1L, 100L);
        final int result2 = articleViewCountBackUpRepository.updateViewCount(1L, 300L);
        final int result3 = articleViewCountBackUpRepository.updateViewCount(1L, 200L);

        then(result1).isEqualTo(1);
        then(result2).isEqualTo(1);
        then(result3).isEqualTo(0);

        ArticleViewCount articleViewCount = em.find(ArticleViewCount.class, 1L);
        then(articleViewCount.getViewCount()).isEqualTo(300L);
    }
}