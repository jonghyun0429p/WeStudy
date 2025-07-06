package com.westudy.study.mapper;

import com.westudy.study.entity.StudyParticipant;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface StudyParticipantMapper {
    void insertStudyParticipant(StudyParticipant studyParticipant);
    StudyParticipant findByUserId(long userId);
    StudyParticipant findByStudyId(long StudyId);
    int countStudyMember(long studyId);
    void updateStudyParticipant(Map<String, Object> params);
    void deleteByUserId(long id);
    void deleteByStudyId(long id);
}
