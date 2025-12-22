package com.ssafy.dash.problem.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 스킬 트리 API 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillTreeResponse {
    private List<SkillTreeNode> families; // 8개 Family 노드 (각각 하위 태그 포함)
    private Integer totalTags; // 전체 태그 수
    private Integer masteredTags; // 마스터한 태그 수 (10문제 이상)
    private Integer learningTags; // 학습 중인 태그 수 (1-9문제)
    private Integer lockedTags; // 잠긴 태그 수 (0문제)
    private Double overallProgress; // 전체 진행률
}
