package com.westudy.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailDBDTO {
    private Long postId;
    private String title;
    private String content;
    private String category;
    private int views;
    private Long authorId; // 작성자 식별자 추가
    private String nickname; // 작성자 닉네임
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String address;
    private Double latitude;
    private Double longitude;
    private Long studyId;
}
