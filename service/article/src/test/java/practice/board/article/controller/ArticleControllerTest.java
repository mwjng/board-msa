package practice.board.article.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import practice.board.article.service.response.ArticlePageResponse;
import practice.board.article.service.response.ArticleResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArticleControllerTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest() {
        final ArticleResponse response = create(new ArticleCreateRequest(
            "hi", "my content", 1L, 1L
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
            .uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse.class);
    }

    @Test
    void readTest() {
        final ArticleResponse response = read(140985467962757120L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
            .uri("/v1/articles/{articleId}", articleId)
            .retrieve()
            .body(ArticleResponse.class);
    }

    @Test
    void updateTest() {
        final ArticleResponse response = update(140985467962757120L, new ArticleUpdateRequest("hi 2", "my content 2"));
        System.out.println("response = " + response);
    }

    ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        return restClient.put()
            .uri("/v1/articles/{articleId}", articleId)
            .body(request)
            .retrieve()
            .body(ArticleResponse.class);
    }

    @Test
    void deleteTest() {
        restClient.delete()
            .uri("/v1/articles/{articleId}", 140985467962757120L)
            .retrieve()
            .body(Void.class);
    }

    @Test
    void readAllTest() {
        final ArticlePageResponse response = restClient.get()
            .uri("v1/articles?boardId=1&pageSize=30&page=5000")
            .retrieve()
            .body(ArticlePageResponse.class);
        System.out.println("response = " + response);
    }

    @Test
    void readAllInfiniteScrollTest() {
        final List<ArticleResponse> articleResponses = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
            .retrieve()
            .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
            });

        System.out.println("firstPage");
        for (ArticleResponse articleResponse : articleResponses) {
            System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }

        Long lastArticleId = articleResponses.getLast().getArticleId();
        final List<ArticleResponse> articleResponses2 = restClient.get()
            .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
            .retrieve()
            .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
            });

        System.out.println("secondPage");
        for (ArticleResponse articleResponse : articleResponses2) {
            System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }
    }

    @Getter
    @AllArgsConstructor
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}