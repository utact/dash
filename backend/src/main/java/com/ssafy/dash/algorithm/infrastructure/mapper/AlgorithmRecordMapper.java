package com.ssafy.dash.algorithm.infrastructure.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.dash.algorithm.domain.AlgorithmRecord;
import com.ssafy.dash.study.application.dto.result.StudyStatsResult;

@Mapper
public interface AlgorithmRecordMapper {

    void insert(AlgorithmRecord record);
    
    AlgorithmRecord selectById(Long id);
    
    List<AlgorithmRecord> selectAll();
    
    List<AlgorithmRecord> selectByUserId(Long userId);

    List<AlgorithmRecord> selectByStudyId(Long studyId);
    
    void save(AlgorithmRecord record);
    
    void update(AlgorithmRecord record);

    StudyStatsResult countsByStudyId(Long studyId);
    
    int delete(Long id);

}
