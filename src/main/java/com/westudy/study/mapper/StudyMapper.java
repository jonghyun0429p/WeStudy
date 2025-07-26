package com.westudy.study.mapper;

import com.westudy.study.dto.StudyParticipanResponseDTO;
import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.dto.StudyUpdateDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.enums.StudyStates;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudyMapper {
    void insertStudy(Study study);
    List<StudyResponseDTO> findStudy(int size, int offset);
    List<StudyResponseDTO> findSearchStudy(String keyword, int page);
    StudyResponseDTO findByStudyId(long id);
    StudyResponseDTO findByPostId(long id);
    long countStudy();
    long countSearchStudy(String keyword);
    void updateStudy(StudyUpdateDTO studyUpdateDTO);
    void updateStudyState(StudyStates studyStates, long id);
    void deleteStudy(long id);

    long findStudyWriter(long id);


}
