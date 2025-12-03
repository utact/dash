package com.ssafy.dash.onboarding.application.dto.result;

public record RepositorySetupResult(
        Long userId, String repositoryName, boolean webhookConfigured
) {
    
}
