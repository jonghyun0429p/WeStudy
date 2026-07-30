package com.westudy.chat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDTO {
    private Long studyId;
    private Long postId;
    private String title;
    private String location;
    private int maxMember;
    private int approvedMemberCount;
    private String state;
    private boolean isHost;
    private String lastMessage;
}
