package com.ssafy.dash.user.presentation.dto.request;

import com.ssafy.dash.user.application.dto.command.UserCreateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private String username;
    private String email;

    public UserCreateCommand toCommand() {
        return new UserCreateCommand(username, email);
    }

}
