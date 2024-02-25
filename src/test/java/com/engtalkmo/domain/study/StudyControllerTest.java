package com.engtalkmo.domain.study;

import com.engtalkmo.domain.study.dto.CreateStudyRequest;
import com.engtalkmo.domain.study.dto.UpdateStudyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class StudyControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired WebApplicationContext context;
    @Autowired StudyRepository studyRepository;
    
    @BeforeEach
    public void mockMvcSetUp() {
        MockMvcBuilders.webAppContextSetup(context).build();
        studyRepository.deleteAll();
    }
    
    @DisplayName("createStudy: 스터디 추가에 성공한다.")
    @Test
    void createStudy() throws Exception {
        // given
        final String url = "/api/study";
        final String author = "hui"; // String -> Entity 객체로 변경 예정
        final String title = "spring 스터디";
        final String content = "spring 공부 같이 하실 분 구합니다.";
        final CreateStudyRequest createStudyRequest = new CreateStudyRequest(author, title, content);

        String requestBody = objectMapper.writeValueAsString(createStudyRequest);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // given
        result.andExpect(status().isCreated());

        Study findStudy = studyRepository.findByAuthor(author)
                .orElseThrow(IllegalArgumentException::new);
        assertThat(findStudy.getTitle()).isEqualTo(title);
        assertThat(findStudy.getContent()).isEqualTo(content);
    }

    @DisplayName("findMainStudies: 스터디 목록 조회에 성공한다.")
    @Test
    void findMainStudies() throws Exception {
        // given
        final String url = "/api/studies";
        final String author = "hui"; // String -> Entity 객체로 변경 예정
        final String title = "spring 스터디";
        final String content = "spring 공부 같이 하실 분 구합니다.";
        Study study = new CreateStudyRequest(author, title, content).toEntity();
        studyRepository.save(study);

        // when
        ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        // given
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title))
                .andExpect(jsonPath("$[0].content").value(content));
    }

    @DisplayName("findStudy: 스터디 조회에 성공한다.")
    @Test
    void findStudy() throws Exception {
        // given
        final String url = "/api/study/{id}";
        final String author = "hui"; // String -> Entity 객체로 변경 예정
        final String title = "spring 스터디";
        final String content = "spring 공부 같이 하실 분 구합니다.";

        Study study = studyRepository.save(
                new CreateStudyRequest(author, title, content).toEntity());

        // when
        ResultActions result = mockMvc.perform(get(url, study.getId()));

        // given
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));
    }

    @DisplayName("deleteStudy: 스터디 삭제에 성공한다.")
    @Test
    void deleteStudy() throws Exception {
        // given
        final String url = "/api/study/{id}";
        final String author = "hui"; // String -> Entity 객체로 변경 예정
        final String title = "spring 스터디";
        final String content = "spring 공부 같이 하실 분 구합니다.";

        Study study = studyRepository.save(
                new CreateStudyRequest(author, title, content).toEntity());

        // when
        ResultActions result = mockMvc.perform(delete(url, study.getId()))
                .andExpect(status().isOk());

        // given
        List<Study> studies = studyRepository.findAll();
        assertThat(studies).isEmpty();
    }

    @DisplayName("updateStudy: 스터디 수정에 성공한다.")
    @Test
    void updateStudy() throws Exception {
        // given
        final String url = "/api/study/{id}";
        final String author = "hui"; // String -> Entity 객체로 변경 예정
        final String title = "spring 스터디";
        final String content = "spring 공부 같이 하실 분 구합니다.";

        final String newTitle = "JPA 스터디";
        final String newContent = "JPA 공부 같이 하실 분 구합니다.";
        UpdateStudyRequest updateStudyRequest = new UpdateStudyRequest(newTitle, newContent);

        Study study = studyRepository.save(
                new CreateStudyRequest(author, title, content).toEntity());

        // when
        ResultActions result = mockMvc.perform(put(url, study.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudyRequest)));

        // given
        result.andExpect(status().isOk());

        Study findStudy = studyRepository.findById(study.getId()).get();
        assertThat(findStudy.getTitle()).isEqualTo(newTitle);
        assertThat(findStudy.getContent()).isEqualTo(newContent);
    }
}