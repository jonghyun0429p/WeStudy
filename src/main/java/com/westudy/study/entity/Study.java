package com.westudy.study.entity;

import com.westudy.study.enums.StudyStates;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Study {
    private long id;
    private long userId;
    private Long postId;
    private String title;
    private String location;
    private int maxMember;
    private LocalDateTime deadline;
    private StudyStates state;
    private String techStacks;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
}
