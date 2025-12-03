package com.ssafy.dash.user.application.dto.command;

public record UserCreateCommand(
        String username, String email
) {

}
