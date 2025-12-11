package com.ssafy.dash.study.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ssafy.dash.study.domain.Study;
import com.ssafy.dash.study.domain.StudyRepository;
import com.ssafy.dash.study.infrastructure.mapper.StudyMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl implements StudyRepository {

    private final StudyMapper studyMapper;

    @Override
    public void save(Study study) {
        studyMapper.save(study);
    }

    @Override
    public Optional<Study> findById(Long id) {
        return studyMapper.findById(id);
    }

    @Override
    public List<Study> findAll() {
        return studyMapper.findAll();
    }

    @Override
    public void update(Study study) {
        studyMapper.update(study);
    }

    @Override
    public boolean delete(Long id) {
        return studyMapper.delete(id) > 0;
    }
}
