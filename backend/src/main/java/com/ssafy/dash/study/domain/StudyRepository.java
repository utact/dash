package com.ssafy.dash.study.domain;

import java.util.List;
import java.util.Optional;

public interface StudyRepository {

    void save(Study study);

    Optional<Study> findById(Long id);

    List<Study> findAll();

    void update(Study study);

    boolean delete(Long id);

}
