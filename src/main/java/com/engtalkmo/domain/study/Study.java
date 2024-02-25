package com.engtalkmo.domain.study;

import com.engtalkmo.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "Study", indexes = {
        @Index(name = "idx_study_recruiting", columnList = "recruiting")
})
public class Study extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "likes", nullable = false)
    private Long likes;

    @Column(name = "location")
    private String location;

    @Column(name = "banner_image")
    private String bannerImage;

    @Column(name = "use_banner")
    private boolean useBanner;

    private boolean published;
    private LocalDateTime publishedDate;

    private boolean recruiting;
    private LocalDateTime recruitingDate;

    private boolean closed;
    private LocalDateTime closedDate;

    private LocalDateTime deadline;

    @Builder
    public Study(String author, String title, String content) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.likes = 0L;
        this.recruiting = true;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
