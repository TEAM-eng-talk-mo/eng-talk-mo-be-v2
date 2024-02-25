package com.engtalkmo.domain.study;

import com.engtalkmo.domain.study.dto.CreateStudyRequest;
import com.engtalkmo.domain.study.dto.StudyResponse;
import com.engtalkmo.domain.study.dto.UpdateStudyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;

    public Long saveStudy(CreateStudyRequest request) {
        Study study = studyRepository.save(request.toEntity());
        return study.getId();
    }

    public List<StudyResponse> findMainStudies() {
        return studyRepository.findAll().stream()
                .map(StudyResponse::new)
                .toList();
    }

    @Transactional
    public Long updateStudy(long id, UpdateStudyRequest request) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not fount: " + id));
        study.update(request.title(), request.content());
        return study.getId();
    }
}
