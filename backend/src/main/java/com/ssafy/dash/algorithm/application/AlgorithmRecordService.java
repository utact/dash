package com.ssafy.dash.algorithm.application;

import com.ssafy.dash.algorithm.application.dto.command.AlgorithmRecordCreateCommand;
import com.ssafy.dash.algorithm.application.dto.command.AlgorithmRecordUpdateCommand;
import com.ssafy.dash.algorithm.application.dto.result.AlgorithmRecordResult;
import com.ssafy.dash.algorithm.domain.AlgorithmRecord;
import com.ssafy.dash.algorithm.domain.AlgorithmRecordRepository;
import com.ssafy.dash.algorithm.domain.StudyStats;
import com.ssafy.dash.algorithm.domain.exception.AlgorithmRecordNotFoundException;
import com.ssafy.dash.user.domain.UserRepository;
import com.ssafy.dash.user.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;
import com.ssafy.dash.defense.application.DefenseService;
import com.ssafy.dash.mockexam.application.MockExamService;
import com.ssafy.dash.battle.application.BattleService;

@Service
public class AlgorithmRecordService {

    private final AlgorithmRecordRepository algorithmRecordRepository;
    private final UserRepository userRepository;
    private final com.ssafy.dash.study.application.StudyMissionService studyMissionService;
    private final DefenseService defenseService;
    private final MockExamService mockExamService;
    private final BattleService battleService;
    private final com.ssafy.dash.study.application.StudyService studyService;

    public AlgorithmRecordService(AlgorithmRecordRepository algorithmRecordRepository, UserRepository userRepository,
            com.ssafy.dash.study.application.StudyMissionService studyMissionService,
            DefenseService defenseService, MockExamService mockExamService, BattleService battleService,
            @org.springframework.context.annotation.Lazy com.ssafy.dash.study.application.StudyService studyService) {
        this.algorithmRecordRepository = algorithmRecordRepository;
        this.userRepository = userRepository;
        this.studyMissionService = studyMissionService;
        this.defenseService = defenseService;
        this.mockExamService = mockExamService;
        this.battleService = battleService;
        this.studyService = studyService;
    }

    @Transactional
    public AlgorithmRecordResult create(AlgorithmRecordCreateCommand command) {
        var user = userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));

        LocalDateTime now = LocalDateTime.now();
        AlgorithmRecord record = AlgorithmRecord.create(
                command.userId(),
                user.getStudyId(),
                command.problemNumber(),
                command.title(),
                command.language(),
                command.code(),
                now);

        algorithmRecordRepository.save(record);

        // Log Count Increase (1 Log per solution)
        user.addLogs(1);
        userRepository.update(user);

        // 스터디 미션, 디펜스, 모의고사 완료 체크
        try {
            int problemId = Integer.parseInt(command.problemNumber());
            studyMissionService.checkAndMarkCompleted(command.userId(), problemId);
            Integer defenseStreak = defenseService.verifyDefense(command.userId(), problemId);
            if (defenseStreak != null) {
                record.setTag("DEFENSE");
                record.setDefenseStreak(defenseStreak);
                algorithmRecordRepository.update(record);
            }
            mockExamService.verifyExam(command.userId(), problemId);
            battleService.verifyBattleProblem(command.userId(), problemId);
        } catch (NumberFormatException e) {
            // ignore non-integer problem numbers
        }

        // 스터디 streak 갱신 (O(1) 연산)
        if (user.getStudyId() != null) {
            studyService.updateStreakOnSolve(user.getStudyId());
        }

        return AlgorithmRecordResult.from(record);
    }

    @Transactional(readOnly = true)
    public AlgorithmRecordResult findById(Long id) {
        AlgorithmRecord record = algorithmRecordRepository.findById(id)
                .orElseThrow(() -> new AlgorithmRecordNotFoundException(id));

        return AlgorithmRecordResult.from(record);
    }

    @Transactional(readOnly = true)
    public List<AlgorithmRecordResult> findAll() {

        return algorithmRecordRepository.findAll().stream()
                .map(AlgorithmRecordResult::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlgorithmRecordResult> findByUserId(Long userId) {

        return algorithmRecordRepository.findByUserId(userId).stream()
                .map(AlgorithmRecordResult::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlgorithmRecordResult> findByStudyId(Long studyId) {

        return algorithmRecordRepository.findByStudyId(studyId).stream()
                .map(AlgorithmRecordResult::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AlgorithmRecordResult update(Long id, AlgorithmRecordUpdateCommand command) {
        AlgorithmRecord record = algorithmRecordRepository.findById(id)
                .orElseThrow(() -> new AlgorithmRecordNotFoundException(id));

        LocalDateTime now = LocalDateTime.now();
        record.applyUpdate(command.problemNumber(), command.title(), command.language(), command.code(), now);

        algorithmRecordRepository.update(record);

        return AlgorithmRecordResult.from(record);
    }

    @Transactional
    public void delete(Long id) {
        boolean deleted = algorithmRecordRepository.delete(id);
        if (!deleted) {
            throw new AlgorithmRecordNotFoundException(id);
        }
    }

    /**
     * 스터디 간 레코드 마이그레이션 (스터디 전체)
     */
    @Transactional
    public void migrateStudyId(Long oldStudyId, Long newStudyId) {
        algorithmRecordRepository.migrateStudyId(oldStudyId, newStudyId);
    }

    /**
     * 특정 사용자의 레코드를 다른 스터디로 마이그레이션
     */
    @Transactional
    public void migrateUserRecords(Long userId, Long oldStudyId, Long newStudyId) {
        algorithmRecordRepository.migrateUserRecords(userId, oldStudyId, newStudyId);
    }

    /**
     * 스터디 통계 조회
     */
    @Transactional(readOnly = true)
    public StudyStats getStudyStats(Long studyId) {
        return algorithmRecordRepository.countsByStudyId(studyId);
    }

    /**
     * 스터디 활동 날짜 목록 조회 (streak 계산용)
     */
    @Transactional(readOnly = true)
    public List<String> getActivityDates(Long studyId) {
        return algorithmRecordRepository.findActivityDatesByStudyId(studyId);
    }

    /**
     * 스터디의 연속 활동 일수 계산 (대시보드와 동일한 로직)
     * 오늘부터 거슬러 올라가면서 연속으로 활동이 있었던 날 수를 계산
     */
    @Transactional(readOnly = true)
    public int calculateStreak(Long studyId) {
        List<String> activityDates = getActivityDates(studyId);
        if (activityDates == null || activityDates.isEmpty()) {
            return 0;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        java.util.Set<java.time.LocalDate> activitySet = activityDates.stream()
                .map(java.time.LocalDate::parse)
                .collect(java.util.stream.Collectors.toSet());

        int streak = 0;
        java.time.LocalDate checkDate = today;

        // 오늘부터 시작해서 연속으로 활동이 있는 날을 카운트
        while (activitySet.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        // 오늘 활동이 없으면 어제부터 체크
        if (streak == 0) {
            checkDate = today.minusDays(1);
            while (activitySet.contains(checkDate)) {
                streak++;
                checkDate = checkDate.minusDays(1);
            }
        }

        return streak;
    }

}
