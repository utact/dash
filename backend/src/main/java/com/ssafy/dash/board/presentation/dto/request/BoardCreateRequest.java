package com.ssafy.dash.board.presentation.dto.request;

import com.ssafy.dash.board.application.dto.command.BoardCreateCommand;
import com.ssafy.dash.board.application.dto.command.InitialCommentCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    private String title;
    private String content;
    private Long userId;
    private Long algorithmRecordId; // nullable
    private String boardType; // GENERAL | CODE_REVIEW
    private String visibility; // Added field
    private List<InitialCommentRequest> initialComments;

    public BoardCreateCommand toCommand() {
        List<InitialCommentCommand> commentCommands = (initialComments != null)
                ? initialComments.stream().map(InitialCommentRequest::toCommand).collect(Collectors.toList())
                : Collections.emptyList();

        // Correct constructor order: title, content, userId, algorithmRecordId,
        // boardType, visibility, initialComments
        return new BoardCreateCommand(title, content, userId, algorithmRecordId, boardType, visibility,
                commentCommands);
    }

}
