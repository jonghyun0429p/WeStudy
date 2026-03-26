package com.westudy.alarm.dto;

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
public class AlarmResponseDTO {
    private Long id;
    private AlarmType type;
    private String content;
    private String targetUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
}
