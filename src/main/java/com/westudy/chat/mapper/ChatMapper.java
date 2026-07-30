package com.westudy.chat.mapper;

import com.westudy.chat.dto.ChatRoomResponseDTO;
import com.westudy.chat.dto.ChatMessageResponseDTO;
import com.westudy.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatMapper {
    void insertMessage(ChatMessage chatMessage);
    List<ChatMessageResponseDTO> findMessagesByStudyId(@Param("studyId") Long studyId);
    List<ChatRoomResponseDTO> findChatRoomsByUserId(@Param("userId") Long userId);
}
