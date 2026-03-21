package com.westudy.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponseDTO {
    private Long postId;
    private String title;
    private String content;
    private String category;
    private int views;
    private String writerNickname;
    private boolean isWriter;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long studyId;
}
