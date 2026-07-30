package com.westudy.chat.controller;

import com.westudy.chat.dto.ChatRoomResponseDTO;
import com.westudy.chat.dto.ChatMessageRequestDTO;
import com.westudy.chat.dto.ChatMessageResponseDTO;
import com.westudy.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Chat Controller", description = "스터디 채팅 API")
public class ChatController {

    private final ChatService chatService;

    /**
     * 메시지 송수신 처리
     */
    @MessageMapping("/chat/{studyId}")
    @SendTo("/topic/study/{studyId}")
    public ChatMessageResponseDTO processMessage(@DestinationVariable("studyId") Long studyId,
            @Payload ChatMessageRequestDTO chatRequestDTO) {
        log.info("채팅 메시지 전송 요청 - studyId: {}, message: {}", studyId, chatRequestDTO.getMessage());
        chatRequestDTO.setStudyId(studyId);

        // 메시지 저장 및 권한 검사
        ChatMessageResponseDTO responseDTO = chatService.saveMessage(chatRequestDTO);

        return responseDTO;
    }

    /**
     * 기존 채팅 내역 로드
     */
    @GetMapping("/api/chat/{studyId}/messages")
    @Operation(summary = "과거 채팅 내역 조회", description = "스터디 채팅방 진입 시 과거 내역을 로드합니다.")
    public ResponseEntity<List<ChatMessageResponseDTO>> getChatHistory(@PathVariable("studyId") Long studyId) {
        log.info("과거 채팅 내역 불러오기 요청 - studyId: {}", studyId);
        List<ChatMessageResponseDTO> messageList = chatService.getChatHistory(studyId);
        return ResponseEntity.ok(messageList);
    }

    /**
     * 내가 가입된 실시간 채팅방 목록 조회
     */
    @GetMapping("/api/chat/rooms")
    @Operation(summary = "내가 가입된 채팅방 목록 조회", description = "내가 방장이거나 승인된 스터디 채팅방 리스트를 가져옵니다.")
    public ResponseEntity<List<ChatRoomResponseDTO>> getMyChatRooms() {
        log.info("내가 가입된 채팅방 목록 불러오기");
        List<ChatRoomResponseDTO> rooms = chatService.getMyChatRooms();
        return ResponseEntity.ok(rooms);
    }
}
