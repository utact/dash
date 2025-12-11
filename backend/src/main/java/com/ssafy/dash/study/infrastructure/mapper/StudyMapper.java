package com.ssafy.dash.study.infrastructure.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.dash.study.domain.Study;

@Mapper
public interface StudyMapper {

    void save(Study study);

    Optional<Study> findById(Long id);

    List<Study> findAll();

    void update(Study study);

    int delete(Long id);

}
