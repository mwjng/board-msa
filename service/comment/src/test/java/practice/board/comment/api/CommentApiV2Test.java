package practice.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import practice.board.comment.service.response.CommentPageResponse;
import practice.board.comment.service.response.CommentResponse;

import java.util.List;

public class CommentApiV2Test {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
        void create() {
        final CommentResponse response1 = create(new CommentCreateRequestV2(1L, "my comment1", null, 1L));
        final CommentResponse response2 = create(new CommentCreateRequestV2(1L, "my comment2", response1.getPath(), 1L));
        final CommentResponse response3 = create(new CommentCreateRequestV2(1L, "my comment3", response2.getPath(), 1L));

        System.out.println("response1.getPath() = " + response1.getPath());
        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getPath() = " + response2.getPath());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getPath() = " + response3.getPath());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());

        /**
         * response1.getPath() = 00002
         * response1.getCommentId() = 152104485143728128
         * 	response2.getPath() = 0000200000
         * 	response2.getCommentId() = 152104485508632576
         * 		response3.getPath() = 000020000000000
         * 		response3.getCommentId() = 152104485571547136
         */
    }

    CommentResponse create(CommentCreateRequestV2 request) {
        return restClient.post()
            .uri("/v2/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse.class);
    }

    @Test
    void read() {
        final CommentResponse response = restClient.get()
            .uri("/v2/comments/{commentId}", 152104485143728128L)
            .retrieve()
            .body(CommentResponse.class);
        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
            .uri("/v2/comments/{commentId}", 15210485143728128L)
            .retrieve()
            .toBodilessEntity();
    }

    @Test
    void readAll() {
        final CommentPageResponse response = restClient.get()
            .uri("/v2/comments?articleId=1&pageSize=10&page=1")
            .retrieve()
            .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScroll() {
        final List<CommentResponse> responses1 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });

        System.out.println("firstPage");
        for (CommentResponse response : responses1) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        final String lastPath = responses1.getLast().getPath();
        final List<CommentResponse> responses2 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=%s"
                .formatted(lastPath))
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });

        System.out.println("secondPage");
        for (CommentResponse response : responses2) {
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }
    }

    @Test
    void countTest() {
        final CommentResponse commentResponse = create(new CommentCreateRequestV2(2L, "content", null, 1L));

        final Long count1 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long.class);
        System.out.println("count1 = " + count1); // 1

        restClient.delete()
            .uri("/v2/comments/{commentId}", commentResponse.getCommentId())
            .retrieve()
            .toBodilessEntity();

        final Long count2 = restClient.get()
            .uri("/v2/comments/articles/{articleId}/count", 2L)
            .retrieve()
            .body(Long.class);
        System.out.println("count2 = " + count2); // 0
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }
}
