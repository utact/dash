package com.ssafy.dash.study.application.dto.result;

import java.time.LocalDate;
import java.util.List;

import com.ssafy.dash.study.domain.StudyMission.MissionStatus;

/**
 * 미션 진행 현황 Result DTO
 */
public record MissionWithProgressResult(
        Long id,
        Integer week,
        String title,
        List<Integer> problemIds,
        List<MissionProblemInfo> problems, // NEW: 문제 상세 정보
        String sourceType,
        LocalDate deadline,
        MissionStatus status,
        int solvedCount,
        int totalProblems,
        List<Integer> solvedProblemIds, // NEW: 현재 사용자가 해결한 문제 ID 목록
        List<MemberProgressResult> memberProgressList) {
}
