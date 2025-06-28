package com.westudy.post.dto;

import com.westudy.post.enums.PostCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostInsertDTO {
    private PostCategory postCategory;
    private boolean isNotice;
    private String title;
    private String Content;
}
