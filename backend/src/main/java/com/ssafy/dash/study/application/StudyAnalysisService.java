package com.ssafy.dash.study.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.dash.analytics.domain.UserTagStat;
import com.ssafy.dash.analytics.domain.UserTagStatRepository;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * 스터디 팀 분석 서비스
 * - 멤버별 태그 통계 조회 (육각차트용)
 * - 팀 평균 분석
 * - 커리큘럼 생성
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyAnalysisService {

    private final UserRepository userRepository;
    private final UserTagStatRepository userTagStatRepository;

    // 육각차트에 표시할 대표 태그 6개
    private static final List<String> CHART_TAGS = List.of(
            "dp", "graphs", "implementation", "math", "data_structures", "greedy");

    /**
     * 스터디 팀 분석 - 멤버별 태그 통계와 팀 평균
     */
    public StudyAnalysisResult analyzeStudy(Long studyId) {
        // 스터디 멤버 조회
        List<User> members = userRepository.findByStudyId(studyId);

        if (members.isEmpty()) {
            return StudyAnalysisResult.empty();
        }

        // 멤버별 태그 통계 수집
        List<MemberTagStats> memberStats = new ArrayList<>();
        Map<String, List<Double>> tagRatesMap = new HashMap<>();

        for (String tag : CHART_TAGS) {
            tagRatesMap.put(tag, new ArrayList<>());
        }

        for (User member : members) {
            List<UserTagStat> tagStats = userTagStatRepository.findByUserId(member.getId());
            Map<String, Double> memberTagRates = new HashMap<>();

            for (String targetTag : CHART_TAGS) {
                UserTagStat stat = tagStats.stream()
                        .filter(s -> targetTag.equals(s.getTagKey()))
                        .findFirst()
                        .orElse(null);

                double rate = (stat != null && stat.getTotal() != null && stat.getTotal() > 0)
                        ? (double) stat.getSolved() / stat.getTotal() * 100
                        : 0.0;

                memberTagRates.put(targetTag, rate);
                tagRatesMap.get(targetTag).add(rate);
            }

            memberStats.add(new MemberTagStats(
                    member.getId(),
                    member.getUsername(),
                    member.getSolvedacTier(),
                    memberTagRates));
        }

        // 팀 평균 계산
        Map<String, Double> teamAverages = new HashMap<>();
        List<WeaknessTag> weaknessTags = new ArrayList<>();

        for (String tag : CHART_TAGS) {
            List<Double> rates = tagRatesMap.get(tag);
            double avg = rates.stream().mapToDouble(d -> d).average().orElse(0.0);
            teamAverages.put(tag, avg);
            weaknessTags.add(new WeaknessTag(tag, avg));
        }

        // 약점 태그 정렬 (낮은 순)
        weaknessTags.sort((a, b) -> Double.compare(a.averageRate(), b.averageRate()));

        // 평균 티어 계산
        double avgTier = members.stream()
                .filter(m -> m.getSolvedacTier() != null)
                .mapToInt(User::getSolvedacTier)
                .average()
                .orElse(0.0);

        return new StudyAnalysisResult(
                memberStats,
                teamAverages,
                avgTier,
                weaknessTags.subList(0, Math.min(3, weaknessTags.size())));
    }

    // DTO Records
    public record StudyAnalysisResult(
            List<MemberTagStats> memberStats,
            Map<String, Double> teamAverages,
            double averageTier,
            List<WeaknessTag> topWeaknesses) {
        public static StudyAnalysisResult empty() {
            return new StudyAnalysisResult(List.of(), Map.of(), 0.0, List.of());
        }
    }

    public record MemberTagStats(
            Long userId,
            String username,
            Integer tier,
            Map<String, Double> tagRates // tag -> completion rate (%)
    ) {
    }

    public record WeaknessTag(
            String tagKey,
            double averageRate) {
    }
}
