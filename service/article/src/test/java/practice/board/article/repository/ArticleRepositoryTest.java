package practice.board.article.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.board.article.entity.Article;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {

    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findAllTest() {
        final List<Article> articles = articleRepository.findAll(1L, 300000L, 30L);
        log.info("articles.size = {}", articles.size());
        for (Article article : articles) {
            log.info("article = {}", article);
        }
    }

    @Test
    void countTest() {
        final Long count = articleRepository.count(1L, 10000L);
        log.info("count = {}", count);
    }

    @Test
    void findInfiniteScrollTest() {
        List<Article> articles = articleRepository.findAllInfiniteScroll(1L, 30L);
        for (Article article : articles) {
            log.info("articleId = {}", article.getArticleId());
        }

        final Long lastArticleId = articles.getLast().getArticleId();
        final List<Article> articles2 = articleRepository.findAllInfiniteScroll(1L, 30L, lastArticleId);
        for (Article article : articles2) {
            log.info("articleId = {}", article.getArticleId());
        }
    }
}