package com.westudy.studylog.mapper;

import com.westudy.studylog.dto.StudyLogResponseDTO;
import com.westudy.studylog.entity.StudyLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyLogMapper {
    void insertLog(StudyLog studyLog);
    void updateLog(StudyLog studyLog);
    void deleteLog(Long id);
    StudyLog findById(Long id);
    List<StudyLogResponseDTO> findByStudyId(Long studyId);
}
