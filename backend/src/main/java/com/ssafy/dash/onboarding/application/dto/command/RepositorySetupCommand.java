package com.ssafy.dash.onboarding.application.dto.command;

public record RepositorySetupCommand(
        Long userId, String repositoryName
) {
    
}
