package com.westudy.post.dto;

import com.westudy.post.enums.PostCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostResponseDTO {
    private long postId;
    private long views;
    private PostCategory postCategory;
    private String title;
    private String summary;
}
