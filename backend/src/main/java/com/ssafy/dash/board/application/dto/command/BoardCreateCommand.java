package com.ssafy.dash.board.application.dto.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardCreateCommand {

    private final String title;
    private final String content;
    private final Long userId;

}
