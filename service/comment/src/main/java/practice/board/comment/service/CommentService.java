package practice.board.comment.service;

import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.board.comment.entity.Comment;
import practice.board.comment.repository.CommentRepository;
import practice.board.comment.service.request.CommentCreateRequest;
import practice.board.comment.service.response.CommentPageResponse;
import practice.board.comment.service.response.CommentResponse;

import java.util.List;

import static java.util.function.Predicate.not;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(CommentCreateRequest request) {
        Comment parent = findParent(request);
        final Comment comment = commentRepository.save(
            Comment.create(
                snowflake.nextId(),
                request.getContent(),
                parent == null ? null : parent.getParentCommentId(),
                request.getArticleId(),
                request.getWriterId()
            )
        );
        return CommentResponse.from(comment);
    }

    private Comment findParent(final CommentCreateRequest request) {
        final Long parentCommentId = request.getParentCommentId();
        if(parentCommentId == null) {
            return null;
        }
        return commentRepository.findById(parentCommentId)
            .filter(not(Comment::getDeleted))
            .filter(Comment::isRoot)
            .orElseThrow();
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(
            commentRepository.findById(commentId).orElseThrow());
    }

    @Transactional
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
            .filter(not(Comment::getDeleted))
            .ifPresent(comment -> {
                if(hasChildren(comment)) {
                    comment.delete();
                } else {
                    delete(comment);
                }
            });
    }

    private boolean hasChildren(final Comment comment) {
        return commentRepository.countBy(comment.getArticleId(), comment.getCommentId(), 2L) == 2;
    }

    private void delete(final Comment comment) {
        commentRepository.delete(comment);
        if (!comment.isRoot()) {
            commentRepository.findById(comment.getParentCommentId())
                .filter(Comment::getDeleted)
                .filter(not(this::hasChildren))
                .ifPresent(this::delete);
        }
    }

    public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
        return CommentPageResponse.of(
            commentRepository.findAll(articleId, (page - 1) * pageSize, pageSize).stream()
                .map(CommentResponse::from)
                .toList(),
            commentRepository.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        );
    }

    public List<CommentResponse> readAll(Long articleId, Long lastParentCommentId, Long lastCommentId, Long limit) {
        final List<Comment> comments = lastParentCommentId == null || lastCommentId == null ?
            commentRepository.findAllInfiniteScroll(articleId, limit) :
            commentRepository.findAllInfiniteScroll(articleId, lastParentCommentId, lastCommentId, limit);
        return comments.stream()
            .map(CommentResponse::from)
            .toList();
    }
}
