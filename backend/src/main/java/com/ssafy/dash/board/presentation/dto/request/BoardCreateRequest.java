package com.ssafy.dash.board.presentation.dto.request;

import com.ssafy.dash.board.application.dto.command.BoardCreateCommand;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    private String title;
    private String content;
    private Long userId;

    public BoardCreateCommand toCommand() {
        return new BoardCreateCommand(title, content, userId);
    }
    
}
