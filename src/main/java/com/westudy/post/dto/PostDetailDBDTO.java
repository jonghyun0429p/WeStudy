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
    private String nickname; // 작성자
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
