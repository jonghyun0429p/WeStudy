package com.westudy.post.entity;

import com.westudy.post.enums.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Post {
    private long id;
    private long userId;
    private long studyId;
    private long views;
    private boolean isNotice;
    private PostCategory category;
    private String title;
    private String summary;
    private Double latitude;
    private Double longitude;
    private String address;
    private LocalDateTime createAt;
    private LocalDateTime deleteAt;
}
