package com.westudy.post.entity;

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
    private String title;
    private String summary;
    private LocalDateTime createAt;
    private LocalDateTime deleteAt;

}
