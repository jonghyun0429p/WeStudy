package com.westudy.comment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {
    private long commentId;
    private long userId;
    private String writerNickname;
    private String content;
    private LocalDateTime createdAt;
    private boolean isWriter;
    private int likeCount;
    private boolean isLiked;
}
