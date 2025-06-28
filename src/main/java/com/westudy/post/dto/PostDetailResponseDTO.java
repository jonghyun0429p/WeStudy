package com.westudy.post.dto;

import lombok.*;

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
}
