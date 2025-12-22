package com.ssafy.dash.study.infrastructure.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ssafy.dash.study.domain.StudyMissionSubmission;
import com.ssafy.dash.study.domain.StudyMissionSubmissionRepository;
import com.ssafy.dash.study.infrastructure.mapper.StudyMissionSubmissionMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudyMissionSubmissionRepositoryImpl implements StudyMissionSubmissionRepository {

    private final StudyMissionSubmissionMapper mapper;

    @Override
    public void save(StudyMissionSubmission submission) {
        mapper.insert(submission);
    }

    @Override
    public List<StudyMissionSubmission> findByMissionId(Long missionId) {
        return mapper.selectByMissionId(missionId);
    }

    @Override
    public StudyMissionSubmission findByMissionIdAndUserIdAndProblemId(Long missionId, Long userId, Integer problemId) {
        return mapper.selectByMissionIdAndUserIdAndProblemId(missionId, userId, problemId);
    }

    @Override
    public void markCompleted(Long missionId, Long userId, Integer problemId) {
        mapper.markCompleted(missionId, userId, problemId);
    }

    @Override
    public int countCompletedByMissionIdAndUserId(Long missionId, Long userId) {
        return mapper.countCompletedByMissionIdAndUserId(missionId, userId);
    }
}
