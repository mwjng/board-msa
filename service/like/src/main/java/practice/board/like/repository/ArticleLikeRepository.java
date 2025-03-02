package practice.board.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import practice.board.like.entity.ArticleLike;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);
}
