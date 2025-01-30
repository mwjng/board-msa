package practice.board.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import practice.board.comment.service.response.CommentPageResponse;
import practice.board.comment.service.response.CommentResponse;

import java.util.List;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        final CommentResponse response1 = createComment(new CommentCreateRequest(1L, "my content1", null, 1L));
        final CommentResponse response2 = createComment(new CommentCreateRequest(1L, "my content2", response1.getCommentId(), 1L));
        final CommentResponse response3 = createComment(new CommentCreateRequest(1L, "my content3", response1.getCommentId(), 1L));

        System.out.println("commentId=%s".formatted(response1.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response2.getCommentId()));
        System.out.println("\tcommentId=%s".formatted(response3.getCommentId()));

//        commentId=143221655917305856
//            commentId=143221656697446400
//            commentId=143221656772943872
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse.class);
    }

    @Test
    void read() {
        final CommentResponse response = restClient.get()
            .uri("/v1/comments/{commentId}", 143221655917305856L)
            .retrieve()
            .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
//        commentId=143221655917305856
//            commentId=143221656697446400
//            commentId=143221656772943872

        restClient.delete()
            .uri("/v1/comments/{commentId}", 143221656772943872L)
            .retrieve()
            .body(Void.class);
    }

    @Test
    void readAll() {
        final CommentPageResponse response = restClient.get()
            .uri("/v1/comments?articleId=1&page=1&pageSize=10")
            .retrieve()
            .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /**
         * 1번 페이지 수행 결과
         * comment.getCommentId() = 143222094628921344
         * 	comment.getCommentId() = 143222094880579584
         * 	comment.getCommentId() = 143222094926716928
         * comment.getCommentId() = 143231865361223680
         * 	comment.getCommentId() = 143231865403166727
         * comment.getCommentId() = 143231865361223681
         * 	comment.getCommentId() = 143231865403166724
         * comment.getCommentId() = 143231865361223682
         * 	comment.getCommentId() = 143231865403166726
         * comment.getCommentId() = 143231865361223683
         */
    }

    @Test
    void readAllInfiniteScroll() {
        final List<CommentResponse> responses1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });

        System.out.println("firstPage");
        for (CommentResponse comment : responses1) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        final Long lastParentCommentId = responses1.getLast().getParentCommentId();
        final Long lastCommentId = responses1.getLast().getCommentId();

        final List<CommentResponse> responses2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&lastParentCommentId=%s&lastCommentId=%s&pageSize=5"
                .formatted(lastParentCommentId, lastCommentId))
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });

        System.out.println("secondPage");
        for (CommentResponse comment : responses2) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }
}
