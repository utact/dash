package com.ssafy.dash.study.application.dto.result;

import java.util.List;

/**
 * 미션 문제 상세 정보 DTO
 * - 프론트엔드에서 난이도 분포 차트, 태그 분석 등에 사용
 */
public record MissionProblemInfo(
        String problemId, // "1000"
        String title, // "A+B"
        Integer level, // 1 (브론즈5) ~ 30 (루비1)
        List<String> tags // ["수학", "구현"]
) {
}
