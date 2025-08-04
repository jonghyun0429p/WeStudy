package com.westudy.comment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private long id;
    private long userId;
    private long postId;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private LocalDateTime deletedAt;
}
