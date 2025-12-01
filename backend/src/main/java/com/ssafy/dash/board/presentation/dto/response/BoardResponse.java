package com.ssafy.dash.board.presentation.dto.response;

import java.time.LocalDateTime;

import com.ssafy.dash.board.application.dto.result.BoardResult;

public record BoardResponse(
        Long id,
        String title,
        String content,
        Long userId,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static BoardResponse from(BoardResult result) {
        return new BoardResponse(result.id(), result.title(), result.content(), result.userId(), result.authorName(), result.createdAt(), result.updatedAt());
    }

}
