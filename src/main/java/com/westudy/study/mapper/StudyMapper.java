package com.westudy.study.mapper;

import com.westudy.study.dto.MyPageStudyDTO;
import com.westudy.study.dto.StudyResponseDTO;
import com.westudy.study.dto.StudyUpdateDTO;
import com.westudy.study.entity.Study;
import com.westudy.study.enums.StudyStates;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyMapper {
    void insertStudy(Study study);
    List<StudyResponseDTO> findStudy(int size, int offset);
    List<StudyResponseDTO> findSearchStudy(@Param("keyword") String keyword, @Param("size") int size, @Param("page") int page);
    StudyResponseDTO findByStudyId(long id);
    StudyResponseDTO findByPostId(long id);
    long countStudy();
    long countSearchStudy(String keyword);
    void updateStudy(StudyUpdateDTO studyUpdateDTO);
    void updateStudyState(@Param("state") StudyStates studyStates, @Param("id") long id);
    void deleteStudy(long id);

    List<MyPageStudyDTO> findParticipatingStudies(long userId);
    List<MyPageStudyDTO> findOpenedStudies(long userId);
    List<MyPageStudyDTO> findBookmarkedStudies(long userId);

    long findUserIdByStudyId(long id);
    List<Long> findParticipantIds(long studyId);

    void closeExpiredStudies();
    List<Long> findExpiredStudyIds();


    Study findByIdForUpdate(long id);
}
