package com.engtalkmo.domain.study;

import com.engtalkmo.domain.study.dto.CreateStudyRequest;
import com.engtalkmo.domain.study.dto.StudyResponse;
import com.engtalkmo.domain.study.dto.UpdateStudyRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudyController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;

    @GetMapping("/api/study/{id}")
    public ResponseEntity<StudyResponse> findStudy(@PathVariable(name = "id") long id) {
        Study study = studyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        return ResponseEntity.ok()
                .body(new StudyResponse(study));
    }

    @PostMapping("/api/study")
    public ResponseEntity<Long> createStudy(@RequestBody CreateStudyRequest request) {
        Long saveId = studyService.saveStudy(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(saveId);
    }

    @PutMapping("/api/study/{id}")
    public ResponseEntity<Long> updateStudy(@PathVariable(name = "id") long id, @RequestBody UpdateStudyRequest request) {
        Long studyId = studyService.updateStudy(id, request);
        return ResponseEntity.ok()
                .body(studyId);
    }

    @DeleteMapping("/api/study/{id}")
    public ResponseEntity<Void> deleteStudy(@PathVariable(name = "id") long id) {
        studyRepository.deleteById(id);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping("/api/studies")
    public ResponseEntity<List<StudyResponse>> findMainStudies() {
        return ResponseEntity.ok()
                .body(studyService.findMainStudies());
    }
}
