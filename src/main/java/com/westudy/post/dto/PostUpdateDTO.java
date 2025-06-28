package com.westudy.post.dto;

import com.westudy.post.enums.PostCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateDTO {
    private Long postId;
    private String title;
    private String content;
    private PostCategory category;
}
