package com.ssafy.dash.board.application.dto.result;

import java.time.LocalDateTime;

import com.ssafy.dash.board.domain.Board;

public record BoardResult(
        Long id,
        String title,
        String content,
        Long userId,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BoardResult from(Board board, String authorName) {
        return new BoardResult(board.getId(), board.getTitle(), board.getContent(), board.getUserId(), authorName, board.getCreatedAt(), board.getUpdatedAt());
    }

}
