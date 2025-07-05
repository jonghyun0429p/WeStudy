package com.westudy.study.mapper;

import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.dto.StudyUpdateDTO;
import com.westudy.study.entity.Study;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudyMapper {
    void insertStudy(Study study);
    List<StudyResponseDTO> findStudy(int size, int offset);
    List<StudyResponseDTO> findSearchStudy(String keyword, int page);
    Study findByStudyId(long id);
    Study findByPostId(long id);
    long countStudy();
    long countSearchStudy(String keyword);
    void updateStudy(StudyUpdateDTO studyUpdateDTO);
    void deleteStudy(long id);


}
