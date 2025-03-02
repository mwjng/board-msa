package practice.board.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.board.comment.entity.ArticleCommentCount;

public interface ArticleCommentCountRepository extends JpaRepository<ArticleCommentCount, Long> {

    @Modifying
    @Query(
        value = "update article_comment_count set comment_count = comment_count + 1 where article_id = :articleId",
        nativeQuery = true
    )
    int increase(@Param("articleId") Long articleId);

    @Modifying
    @Query(
        value = "update article_comment_count set comment_count = comment_count - 1 where article_id = :articleId",
        nativeQuery = true
    )
    int decrease(@Param("articleId") Long articleId);
}
