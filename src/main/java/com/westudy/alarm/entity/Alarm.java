package com.westudy.alarm.entity;

import com.westudy.alarm.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alarm {
    private Long id;
    private Long receiverId;
    private Long senderId;
    private AlarmType type;
    private String content;
    private String targetUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
}
