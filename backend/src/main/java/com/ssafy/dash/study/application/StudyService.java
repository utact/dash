package com.ssafy.dash.study.application;

import com.ssafy.dash.study.domain.Study;
import com.ssafy.dash.study.domain.StudyRepository;
import com.ssafy.dash.user.domain.User;
import com.ssafy.dash.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Study> findAll() {
        return studyRepository.findAll();
    }

    @Transactional
    public Study createStudy(Long userId, String name) {
        Study study = Study.create(name);
        studyRepository.save(study);
        // Assuming MyBatis sets the ID in the object after insert
        
        joinStudy(userId, study.getId());
        
        return study;
    }

    @Transactional
    public void joinStudy(Long userId, Long studyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Custom check using findById since existsById is missing
        if (studyRepository.findById(studyId).isEmpty()) {
             throw new IllegalArgumentException("Study not found");
        }
        
        user.updateStudy(studyId);
        userRepository.save(user);
    }
}
