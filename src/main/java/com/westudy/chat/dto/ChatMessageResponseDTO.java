package com.westudy.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseDTO {
    private Long id;
    private Long studyId;
    private Long senderId;
    private String senderNickname;
    private String message;
    private LocalDateTime createdAt;
}
