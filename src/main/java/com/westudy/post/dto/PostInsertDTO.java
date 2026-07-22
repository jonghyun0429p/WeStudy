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
    private String content;
    private String address;
    private Double latitude;
    private Double longitude;

    // Study 전용 필드
    private Integer maxMember;
    private java.time.LocalDateTime deadline;
    private String studyCategory;
    private String techStacks;
}
