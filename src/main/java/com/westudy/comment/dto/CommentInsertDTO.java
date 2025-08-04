package com.westudy.comment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentInsertDTO {
    private long id;
    private long postId;
    private String content;
}
