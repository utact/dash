package com.ssafy.dash.comment.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ssafy.dash.common.fixtures.FixtureTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommentTest {

    @Test
    @DisplayName("생성 시 생성/수정 시간이 동일하게 초기화된다")
    void create_ShouldSetTimestamps() {
        LocalDateTime now = FixtureTime.now();

        Comment comment = Comment.create(1L, 1L, null, null, "테스트 댓글", now);

        assertThat(comment.getCreatedAt()).isEqualTo(now);
        assertThat(comment.getUpdatedAt()).isEqualTo(now);
        assertThat(comment.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("대댓글 생성 시 parentId가 설정된다")
    void create_WithParentId_ShouldBeReply() {
        LocalDateTime now = FixtureTime.now();

        Comment reply = Comment.create(1L, 1L, 100L, null, "대댓글입니다", now);

        assertThat(reply.isReply()).isTrue();
        assertThat(reply.getParentId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("라인 댓글 생성 시 lineNumber가 설정된다")
    void create_WithLineNumber_ShouldBeLineComment() {
        LocalDateTime now = FixtureTime.now();

        Comment lineComment = Comment.create(1L, 1L, null, 42, "42번째 라인 댓글", now);

        assertThat(lineComment.isLineComment()).isTrue();
        assertThat(lineComment.getLineNumber()).isEqualTo(42);
    }

    @Test
    @DisplayName("일반 댓글은 parentId와 lineNumber가 모두 null이다")
    void create_GeneralComment_ShouldHaveNullParentAndLine() {
        LocalDateTime now = FixtureTime.now();

        Comment comment = Comment.create(1L, 1L, null, null, "일반 댓글", now);

        assertThat(comment.isReply()).isFalse();
        assertThat(comment.isLineComment()).isFalse();
    }

    @Test
    @DisplayName("부분 업데이트 시 전달된 내용만 변경된다")
    void applyUpdate_ShouldOverwriteContent() {
        Comment comment = Comment.create(1L, 1L, null, null, "원래 내용", FixtureTime.now());

        LocalDateTime updatedAt = FixtureTime.now().plusDays(1);
        comment.applyUpdate("수정된 내용", updatedAt);

        assertThat(comment.getContent()).isEqualTo("수정된 내용");
        assertThat(comment.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("시간 정보 없이 업데이트하면 NullPointerException이 발생한다")
    void applyUpdate_ShouldValidateTimestamp() {
        Comment comment = Comment.create(1L, 1L, null, null, "내용", FixtureTime.now());

        assertThatThrownBy(() -> comment.applyUpdate("새 내용", null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("잘못된 파라미터로 생성하면 IllegalArgumentException이 발생한다")
    void create_ShouldValidateArguments() {
        assertThatThrownBy(() -> Comment.create(0L, 1L, null, null, "내용", FixtureTime.now()))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Comment.create(1L, 0L, null, null, "내용", FixtureTime.now()))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Comment.create(1L, 1L, null, null, "", FixtureTime.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("좋아요 증가/감소가 정상 동작한다")
    void likeCount_ShouldIncrementAndDecrement() {
        Comment comment = Comment.create(1L, 1L, null, null, "내용", FixtureTime.now());

        assertThat(comment.getLikeCount()).isEqualTo(0);

        comment.incrementLikeCount();
        assertThat(comment.getLikeCount()).isEqualTo(1);

        comment.decrementLikeCount();
        assertThat(comment.getLikeCount()).isEqualTo(0);

        // 0 이하로 내려가지 않음
        comment.decrementLikeCount();
        assertThat(comment.getLikeCount()).isEqualTo(0);
    }

}
