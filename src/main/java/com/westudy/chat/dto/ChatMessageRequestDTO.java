package com.westudy.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    private Long studyId;
    private String message;
}
