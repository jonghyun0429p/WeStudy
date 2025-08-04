package com.westudy.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentUpdateDTO {
    private long id;
    private long postId;
    private String content;
}
