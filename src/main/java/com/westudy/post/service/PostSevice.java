package com.westudy.post.service;

import com.westudy.global.exception.BaseException;
import com.westudy.global.util.RequireHelper;
import com.westudy.post.converter.PostConverter;
import com.westudy.post.dto.*;
import com.westudy.post.entity.Post;
import com.westudy.post.entity.PostContent;
import com.westudy.post.enums.PostErrorCode;
import com.westudy.post.mapper.PostContentMapper;
import com.westudy.post.mapper.PostDetailMapper;
import com.westudy.post.mapper.PostMapper;
import com.westudy.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostSevice {

    // private final int POST_SIZE = 12;

    private final PostMapper postMapper;
    private final PostContentMapper postContentMapper;
    private final PostDetailMapper postDetailMapper;
    private final PostConverter postConverter;
    private final com.westudy.study.service.StudyService studyService;

    public PostSevice(PostMapper postMapper, PostContentMapper postContentMapper, PostDetailMapper postDetailMapper,
            PostConverter postConverter, com.westudy.study.service.StudyService studyService) {
        this.postMapper = postMapper;
        this.postContentMapper = postContentMapper;
        this.postDetailMapper = postDetailMapper;
        this.postConverter = postConverter;
        this.studyService = studyService;
    }

    public List<PostResponseDTO> getPostList(int page, int postSize) {
        List<Post> postList = findByNotice();
        postList.addAll(findByPost(page, postSize));

        return postConverter.toResponseDTOList(postList);
    }

    public List<PostResponseDTO> getSearchPostList(String keyword, int page, int postSize) {
        List<PostResponseDTO> postList = postConverter.toResponseDTOList(findByNotice());
        postList.addAll(findSearchPosts(keyword, page, postSize));

        return postList;
    }

    public PostDetailResponseDTO getPostDetailResponse(long postId) {
        log.info("아이디로 추출 시도");
        PostDetailDBDTO postDetailDBDTO = getPostDetail(postId);
        log.info("생성일 확인.");
        log.info(postDetailDBDTO.getCreatedAt().toString());

        // JWT에서 닉네임을 꺼내는 대신 현재 사용자의 ID를 가져옴
        Long currentUserId = SecurityUtil.resolveCurrentUserIdSafely();

        return postConverter.toDetailResponseDTO(postDetailDBDTO, currentUserId);
    }

    public long getPostPage(int postSize) {
        long count = getPostCount();
        if (count % postSize == 0) {
            return getPostCount() / postSize;
        } else {
            return getPostCount() / postSize + 1;
        }

    }

    public void isWriter(long postId) {
        long id = SecurityUtil.getCurrentUserId();
        long postWriter = postMapper.findUserIdByPostId(postId);
        if (id != postWriter) {
            throw new BaseException(PostErrorCode.POST_ID_NOT_SAME);
        }
    }

    // Create
    public void insertPost(Long userId, PostInsertDTO postInsertDTO) {
        log.info("게시글 데이터 변환 및 저장");
        Post post = postConverter.toPost(userId, postInsertDTO);
        postMapper.insertPost(post);
        log.info("게시글 내용 데이터 변환 및 저장");
        PostContent postContent = postConverter.toPostContent(post, postInsertDTO);
        postContentMapper.insertContent(postContent);

        // 스터디 데이터 저장 (카테고리가 STUDY 등 스터디일 경우)
        // 화면에서 STUDY 카테고리가 아니더라도 작성 시 StudyInsertDTO 처리를 분기할 수 있음
        if (postInsertDTO.getPostCategory() == com.westudy.post.enums.PostCategory.STUDY || 
            postInsertDTO.getMaxMember() != null) {
            com.westudy.study.dto.StudyInsertDTO studyDTO = new com.westudy.study.dto.StudyInsertDTO();
            studyDTO.setPost_id(post.getId());
            studyDTO.setTitle(post.getTitle());
            studyDTO.setLocation(postInsertDTO.getAddress());
            studyDTO.setMaxMember(postInsertDTO.getMaxMember() != null ? postInsertDTO.getMaxMember() : 1);
            studyDTO.setDeadline(postInsertDTO.getDeadline());
            studyDTO.setState(com.westudy.study.enums.StudyStates.RECRUITING);
            studyDTO.setTechStacks(postInsertDTO.getTechStacks());
            studyDTO.setCategory(postInsertDTO.getStudyCategory());
            studyService.insertStudy(studyDTO);
        }
    }

    // Read
    public Post findByPostId(long postId) {
        return RequireHelper.requireNonNull(
                postMapper.findByPostId(postId), new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public List<Post> findByUserId(long userId) {
        return RequireHelper.requireNonEmpty(
                postMapper.findByUserId(userId), new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public List<Post> findByNotice() {
        return postMapper.findNotice();
    }

    public List<Post> findByPost(int page, int postSize) {
        return postMapper.findPost(postSize, postSize * (page - 1));
    }

    public PostDetailDBDTO getPostDetail(long postId) {
        return RequireHelper.requireNonNull(
                postDetailMapper.findPostDetailById(postId), new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public List<PostResponseDTO> findSearchPosts(String keyword, int page, int postSize) {
        return RequireHelper.requireNonEmpty(
                postDetailMapper.findSearchPosts(keyword, postSize, postSize * (page - 1)),
                new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public long getPostCount() {
        return postMapper.countPosts();
    }

    public long getSearchedPostCount(String keyword) {
        return postDetailMapper.countSearchPosts(keyword);
    }

    // Update
    public void updatePost(PostUpdateDTO postUpdateDTO) {
        isWriter(postUpdateDTO.getPostId());
        Post oldPost = postMapper.findByPostId(postUpdateDTO.getPostId());
        Post newPost = postConverter.toUpdatePost(oldPost, postUpdateDTO);
        postMapper.updatePost(newPost);
        postContentMapper.updateContent(postConverter.toPostContent(newPost, postUpdateDTO));
    }

    // Delete
    public void deletePost(long postId) {
        isWriter(postId);
        postMapper.deleteByPostId(postId);
    }

}
