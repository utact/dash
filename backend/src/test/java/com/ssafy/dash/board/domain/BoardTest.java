package com.ssafy.dash.board.domain;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ssafy.dash.common.fixtures.FixtureTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoardTest {

    @Test
    @DisplayName("생성 시 생성/수정 시간이 동일하게 초기화된다")
    void create_ShouldSetTimestamps() {
        LocalDateTime now = FixtureTime.now();

        Board board = Board.create(1L, "Title", "Content", null, "GENERAL", "PUBLIC", now);

        assertThat(board.getCreatedAt()).isEqualTo(now);
        assertThat(board.getUpdatedAt()).isEqualTo(now);
        assertThat(board.getBoardType()).isEqualTo("GENERAL");
        assertThat(board.getLikeCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("부분 업데이트 시 전달된 필드만 변경된다")
    void applyUpdate_ShouldOverwriteProvidedFields() {
        Board board = Board.create(1L, "Title", "Content", null, "GENERAL", "PUBLIC", FixtureTime.now());

        LocalDateTime updatedAt = FixtureTime.now().plusDays(1);
        board.applyUpdate("New Title", null, 123L, updatedAt);

        assertThat(board.getTitle()).isEqualTo("New Title");
        assertThat(board.getContent()).isEqualTo("Content");
        assertThat(board.getAlgorithmRecordId()).isEqualTo(123L);
        assertThat(board.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("시간 정보 없이 업데이트하면 NullPointerException이 발생한다")
    void applyUpdate_ShouldValidateTimestamp() {
        Board board = Board.create(1L, "Title", "Content", null, "GENERAL", "PUBLIC", FixtureTime.now());

        assertThatThrownBy(() -> board.applyUpdate("New Title", "New Content", null, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("잘못된 파라미터로 생성하면 IllegalArgumentException이 발생한다")
    void create_ShouldValidateArguments() {
        assertThatThrownBy(() -> Board.create(0L, "", "", null, "GENERAL", "PUBLIC", FixtureTime.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("좋아요 증가/감소가 정상 동작한다")
    void likeCount_ShouldIncrementAndDecrement() {
        Board board = Board.create(1L, "Title", "Content", null, "GENERAL", "PUBLIC", FixtureTime.now());

        assertThat(board.getLikeCount()).isEqualTo(0);

        board.incrementLikeCount();
        assertThat(board.getLikeCount()).isEqualTo(1);

        board.decrementLikeCount();
        assertThat(board.getLikeCount()).isEqualTo(0);

        // 0 이하로 내려가지 않음
        board.decrementLikeCount();
        assertThat(board.getLikeCount()).isEqualTo(0);
    }

}
