package com.westudy.study.mapper;

import com.westudy.study.dto.StudyParticipanResponseDTO;
import com.westudy.study.dto.StudyParticipantUpdateDTO;
import com.westudy.study.entity.StudyParticipant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudyParticipantMapper {
    void insertStudyParticipant(@Param("userId") long userId, @Param("studyId") long studyId);
    StudyParticipant findByUserId(long userId);
    List<StudyParticipanResponseDTO> findByStudyId(long studyId);
    int countStudyMember(long studyId);
    void updateStudyParticipant(StudyParticipantUpdateDTO studyParticipantUpdateDTO);
    void deleteByUserId(long id);
    void deleteByStudyId(long id);
}
