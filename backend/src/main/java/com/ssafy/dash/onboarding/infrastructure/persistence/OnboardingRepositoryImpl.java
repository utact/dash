package com.ssafy.dash.onboarding.infrastructure.persistence;

import com.ssafy.dash.onboarding.domain.Onboarding;
import com.ssafy.dash.onboarding.domain.OnboardingRepository;
import com.ssafy.dash.onboarding.infrastructure.mapper.OnboardingMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OnboardingRepositoryImpl implements OnboardingRepository {

    private final OnboardingMapper mapper;

    public OnboardingRepositoryImpl(OnboardingMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Onboarding> findByUserId(Long userId) {
        return Optional.ofNullable(mapper.selectByUserId(userId));
    }

    @Override
    public void save(Onboarding repository) {
        if (repository.getId() == null) {
            mapper.insert(repository);
        } else {
            mapper.update(repository);
        }
    }

}
