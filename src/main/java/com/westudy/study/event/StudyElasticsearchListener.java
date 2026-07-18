package com.westudy.study.event;

import com.westudy.study.document.StudyDocument;
import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.mapper.StudyMapper;
import com.westudy.study.repository.search.StudySearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyElasticsearchListener {

    private final StudyMapper studyMapper;
    private final StudySearchRepository studySearchRepository;

    @Async
    @EventListener
    public void handleStudyEvent(StudyEvent event) {
        log.info("Elasticsearch 동기화 이벤트 수신 - studyId: {}, action: {}", event.getStudyId(), event.getAction());
        try {
            if ("SAVE".equals(event.getAction())) {
                StudyResponseDTO study = studyMapper.findByStudyId(event.getStudyId());
                if (study != null) {
                    StudyDocument doc = StudyDocument.builder()
                            .id(String.valueOf(study.getId()))
                            .postId(study.getPostId())
                            .userId(study.getUserId())
                            .title(study.getTitle())
                            .location(study.getLocation())
                            .maxMember(study.getMaxMember())
                            .state(study.getState().name())
                            .approvedMemberCount(study.getApprovedMemberCount())
                            .build();
                    studySearchRepository.save(doc);
                    log.info("Elasticsearch 인덱싱 성공 - studyId: {}", event.getStudyId());
                } else {
                    log.warn("인덱싱할 스터디 정보가 존재하지 않습니다 - studyId: {}", event.getStudyId());
                }
            } else if ("DELETE".equals(event.getAction())) {
                studySearchRepository.deleteById(String.valueOf(event.getStudyId()));
                log.info("Elasticsearch 도큐먼트 삭제 성공 - studyId: {}", event.getStudyId());
            }
        } catch (Exception e) {
            log.error("Elasticsearch 동기화 오류 발생 - studyId: {}", event.getStudyId(), e);
        }
    }
}
