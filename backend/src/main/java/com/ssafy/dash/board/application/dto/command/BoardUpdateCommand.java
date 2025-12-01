package com.ssafy.dash.board.application.dto.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardUpdateCommand {

    private final String title;
    private final String content;
    
}
