package com.westudy.chat.service;

import com.westudy.alarm.enums.AlarmType;
import com.westudy.alarm.service.AlarmService;
import com.westudy.chat.dto.ChatMessageRequestDTO;
import com.westudy.chat.dto.ChatMessageResponseDTO;
import com.westudy.chat.entity.ChatMessage;
import com.westudy.chat.mapper.ChatMapper;
import com.westudy.global.exception.BaseException;
import com.westudy.post.enums.PostErrorCode;
import com.westudy.study.entity.StudyParticipant;
import com.westudy.study.enums.StudyErrorCode;
import com.westudy.study.enums.StudyParticipantStatus;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.mapper.StudyParticipantMapper;
import com.westudy.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMapper chatMapper;
    private final StudyParticipantMapper studyParticipantMapper;
    private final StudyMapper studyMapper;
    private final AlarmService alarmService;

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

        // 2. 참여자인지 확인 및 상태 검증 (study_participant 테이블 조회)
        StudyParticipant participant = studyParticipantMapper.findParticipant(userId, studyId);
        if (participant == null || participant.getStatus() != StudyParticipantStatus.APPROVED) {
            log.warn("권한이 없는 사용자의 채팅 시도: userId = {}, status = {}", userId, participant != null ? participant.getStatus() : "null");
            throw new BaseException(StudyErrorCode.STUDY_MEMBER_UNAUTHORIZED);
        }
    }

    @Transactional
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

        String nickname = SecurityUtil.getCurrentNickname();

        // 실시간 알람 발송 (의도적 N+1 빌드업: 반복문 내 연산)
        List<Long> participantIds = studyMapper.findParticipantIds(requestDTO.getStudyId());
        // 방장도 포함해야 함 (목록에 없을 수 있음)
        long leaderId = studyMapper.findUserIdByStudyId(requestDTO.getStudyId());
        if (!participantIds.contains(leaderId)) {
            participantIds.add(leaderId);
        }

        for (Long receiverId : participantIds) {
            if (!receiverId.equals(userId)) {
                alarmService.send(
                        receiverId,
                        userId,
                        AlarmType.CHAT_MESSAGE,
                        String.format("[%s] 새로운 메시지가 도착했습니다: %s", nickname, message.getMessage()),
                        "/page/post/detail?id=" + requestDTO.getStudyId() // TODO: 실제 상세페이지/채팅방 URL로 연결
                );
            }
        }
        
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
