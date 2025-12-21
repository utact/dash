package com.ssafy.dash.board.presentation.dto.request;

import com.ssafy.dash.board.application.dto.command.BoardUpdateCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardUpdateRequest {

    private String title;
    private String content;
    private Long algorithmRecordId; // nullable

    public BoardUpdateCommand toCommand() {
        return new BoardUpdateCommand(title, content, algorithmRecordId);
    }

}
