package practice.board.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import practice.board.article.entity.BoardArticleCount;

public interface BoardArticleCountRepository extends JpaRepository<BoardArticleCount, Long> {

    @Modifying
    @Query(
        value = "update board_article_count set article_count = article_count + 1 where board_id = :boardId",
        nativeQuery = true
    )
    int increase(@Param("boardId") Long boardId);

    @Modifying
    @Query(
        value = "update board_article_count set article_count = article_count - 1 where board_id = :boardId",
        nativeQuery = true
    )
    int decrease(@Param("boardId") Long boardId);
}
