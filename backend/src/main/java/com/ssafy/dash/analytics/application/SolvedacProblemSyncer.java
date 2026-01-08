package com.ssafy.dash.analytics.application;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ssafy.dash.algorithm.infrastructure.mapper.AlgorithmRecordMapper;
import com.ssafy.dash.solvedac.domain.ProblemSearchResult;
import com.ssafy.dash.solvedac.domain.SolvedProblem;
import com.ssafy.dash.solvedac.domain.SolvedacApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Solved.ac 문제 비동기 동기화 담당 서비스
 * 별도 클래스로 분리하여 @Async 프록시가 정상 동작하도록 함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedacProblemSyncer {

    private final SolvedacApiClient solvedacClient;
    private final AlgorithmRecordMapper algorithmRecordMapper;

    /**
     * 사용자가 푼 전체 문제를 비동기로 동기화
     */
    @Async("wsTaskExecutor")

    public void syncAllSolvedProblems(Long userId, String handle) {
        log.info("Started async sync of all solved problems for user {}", userId);
        try {
            int page = 1;
            int totalSynced = 0;
            final int PAGE_SIZE = 50;

            while (true) {
                ProblemSearchResult result = solvedacClient.searchSolvedProblems(handle, page);

                if (result.problems().isEmpty()) {
                    break;
                }

                for (SolvedProblem problem : result.problems()) {
                    // 이미 존재하는지는 Mapper 레벨에서 INSERT IGNORE로 처리
                    algorithmRecordMapper.insertSolvedProblemIfNotExists(
                            userId,
                            String.valueOf(problem.problemId()),
                            problem.title(),
                            problem.level());
                }

                totalSynced += result.problems().size();

                if (page * PAGE_SIZE >= result.totalCount()) {
                    break;
                }

                page++;

                // Rate limit 방지
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            log.info("Async sync completed: {} solved problems for user {}", totalSynced, userId);
        } catch (Exception e) {
            log.warn("Async sync failed for user {}: {}", userId, e.getMessage());
        }
    }
}
