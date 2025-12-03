package com.ssafy.dash.user.application.dto.command;

public record UserUpdateCommand(
        String username, String email
) {

}
