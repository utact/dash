package com.ssafy.dash.user.presentation.dto.request;

import com.ssafy.dash.user.application.dto.command.UserUpdateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    private String username;
    private String email;

    public UserUpdateCommand toCommand() {
        return new UserUpdateCommand(username, email);
    }

}
