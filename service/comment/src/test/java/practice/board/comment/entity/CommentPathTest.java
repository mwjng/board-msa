package practice.board.comment.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CommentPathTest {

    @Test
    void createChildCommentTest() {
        // 00000 <- 생성
        createChildCommentTest(CommentPath.create(""), null, "00000");

        // 00000
        //      00000 <- 생성
        createChildCommentTest(CommentPath.create("00000"), null, "0000000000");

        // 00000
        // 00001 <- 생성
        createChildCommentTest(CommentPath.create(""), "00000", "00001");

        // 0000z
        //      abcdz
        //          zzzzz
        //              zzzzz
        //      abce0 <- 생성
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
    }

    void createChildCommentTest(final CommentPath commentPath, final String descendantsTopPath, final String expectedChildPath) {
        final CommentPath childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);
        assertThat(childCommentPath.getPath()).isEqualTo(expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMaxDepthTest() {
        assertThatThrownBy(() ->
            CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        ).isInstanceOf(IllegalStateException.class)
            .hasMessage("Depth overflow");
    }

    @Test
    void createChildCommentPathIfChunkOverflowTest() {
        // given
        final CommentPath commentPath = CommentPath.create("");

        // when, then
        assertThatThrownBy(() -> commentPath.createChildCommentPath("zzzzz"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Chunk overflow");
    }
}