package com.westudy.chat.service;

import com.westudy.chat.dto.ChatMessageRequestDTO;
import com.westudy.chat.dto.ChatMessageResponseDTO;
import com.westudy.chat.entity.ChatMessage;
import com.westudy.chat.mapper.ChatMapper;
import com.westudy.global.exception.BaseException;
import com.westudy.post.enums.PostErrorCode;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import com.westudy.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final StudyParticipantMapper studyParticipantMapper;
    private final StudyMapper studyMapper;

    /**
     * 메세지 전송 전 권한 체크
     */
    public void validateParticipant(Long studyId, Long userId) {
        log.info("채팅 권한 확인 시작 - userId: {}, studyId: {}", userId, studyId);
        
        // 1. 방장인지 확인 (study 테이블 조회)
        long leaderId = studyMapper.findUserIdByStudyId(studyId);
        if (leaderId == userId) {
            return;
        }

        // 2. 참여자인지 확인 (study_participant 테이블 조회)
        int isParticipant = studyParticipantMapper.findByUserIdAndStudyId(userId, studyId);
        if (isParticipant <= 0) {
            log.warn("권한이 없는 사용자의 채팅 시도: userId = {}", userId);
            throw new BaseException(PostErrorCode.POST_NOT_FOUND); // TODO: 적절한 권한 에러 코드로 대체 가능
        }
    }

    public ChatMessageResponseDTO saveMessage(ChatMessageRequestDTO requestDTO) {
        Long userId = SecurityUtil.getCurrentUserId();
        
        // 전송 시마다 권한 검사 수행
        validateParticipant(requestDTO.getStudyId(), userId);

        ChatMessage message = ChatMessage.builder()
                .studyId(requestDTO.getStudyId())
                .senderId(userId)
                .message(requestDTO.getMessage())
                .build();

        chatMapper.insertMessage(message);

        // 저장 후 가장 최신의 작성자 닉네임을 포함한 데이터 반환 위해 다시 한번 DB 조회 시도 가능 (현재는 세션 또는 별도 조인 사용)
        // 위 Mapper XML에서는 findMessagesByStudyId에서 조인해서 가져오고 있음.
        String nickname = SecurityUtil.getCurrentNickname();
        
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .studyId(message.getStudyId())
                .senderId(userId)
                .senderNickname(nickname)
                .message(message.getMessage())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public List<ChatMessageResponseDTO> getChatHistory(Long studyId) {
        Long userId = SecurityUtil.getCurrentUserId();
        // 읽기 요청 시에도 권한 검증
        validateParticipant(studyId, userId);
        
        return chatMapper.findMessagesByStudyId(studyId);
    }
}
