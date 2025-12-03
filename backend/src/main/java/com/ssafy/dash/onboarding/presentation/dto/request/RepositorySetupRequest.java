package com.ssafy.dash.onboarding.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.ssafy.dash.onboarding.application.dto.command.RepositorySetupCommand;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositorySetupRequest {

    @NotBlank(message = "저장소 이름을 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$", message = "owner/repository 형식으로 입력해주세요.")
    private String repositoryName;

    public RepositorySetupCommand toCommand(Long userId) {
        return new RepositorySetupCommand(userId, repositoryName);
    }
    
}
