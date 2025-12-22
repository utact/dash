package com.ssafy.dash.study.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 미션 제출 현황 (사용자별 문제 완료 여부)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyMissionSubmission {

    private Long id;
    private Long missionId;
    private Long userId;
    private Integer problemId;
    private Boolean completed;
    private LocalDateTime completedAt;

    public static StudyMissionSubmission create(Long missionId, Long userId, Integer problemId) {
        return new StudyMissionSubmission(null, missionId, userId, problemId, false, null);
    }

    public void markCompleted() {
        this.completed = true;
        this.completedAt = LocalDateTime.now();
    }
}
