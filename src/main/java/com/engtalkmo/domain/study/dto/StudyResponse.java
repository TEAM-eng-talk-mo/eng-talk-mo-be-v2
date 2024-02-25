package com.engtalkmo.domain.study.dto;

import com.engtalkmo.domain.study.Study;

import java.time.LocalDateTime;

public record StudyResponse(
        String author,
        String title,
        String content,
        Long likes,
        String bannerImage,
        boolean recruiting,
        LocalDateTime createdDate,
        String location) {

        public StudyResponse(Study study) {
                this(
                        study.getAuthor(),
                        study.getTitle(),
                        study.getContent(),
                        study.getLikes(),
                        study.getBannerImage(),
                        study.isRecruiting(),
                        study.getCreatedDate(),
                        study.getLocation()
                );
        }
}
