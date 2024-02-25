package com.engtalkmo.domain.study.dto;

import com.engtalkmo.domain.study.Study;
import jakarta.validation.constraints.NotBlank;

public record CreateStudyRequest(
        String author,
        @NotBlank(message = "필수 입력입니다.")
        String title,
        @NotBlank(message = "필수 입력입니다.")
        String content) {

    public Study toEntity() {
        return Study.builder()
                .author(this.author)
                .title(this.title)
                .content(this.content)
                .build();
    }
}
