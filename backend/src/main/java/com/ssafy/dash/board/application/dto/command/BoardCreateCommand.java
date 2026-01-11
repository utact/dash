package com.ssafy.dash.board.application.dto.command;

public record BoardCreateCommand(
                String title,
                String content,
                Long userId,
                Long algorithmRecordId, // nullable
                String boardType, // GENERAL | CODE_REVIEW
                String visibility,
                java.util.List<InitialCommentCommand> initialComments // nullable
) {

}
