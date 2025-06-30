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

    private final int POST_SIZE = 20;

    private final PostMapper postMapper;
    private final PostContentMapper postContentMapper;
    private final PostDetailMapper postDetailMapper;
    private final PostConverter postConverter;

    public PostSevice(PostMapper postMapper, PostContentMapper postContentMapper, PostDetailMapper postDetailMapper, PostConverter postConverter) {
        this.postMapper = postMapper;
        this.postContentMapper = postContentMapper;
        this.postDetailMapper = postDetailMapper;
        this.postConverter = postConverter;
    }

    public List<PostResponseDTO> getPostList(int page){
        List<Post> postList = findByNotice();
        postList.addAll(findByPost(page));

        return postConverter.toResponseDTOList(postList);
    }

    public List<PostResponseDTO> getSearchPostList(String keyword, int page){
        List<PostResponseDTO> postList = postConverter.toResponseDTOList(findByNotice());
        postList.addAll(findSearchPosts(keyword, page));

        return postList;
    }

    public PostDetailResponseDTO getPostDetailResponse(long postId){
        log.info("아이디로 추출 시도");
        PostDetailDBDTO postDetailDBDTO = getPostDetail(postId);
        String nickname = SecurityUtil.getCurrentNickname();

        return postConverter.toDetailResponseDTO(postDetailDBDTO, nickname);
    }

    public long getPostPage(){
        long count = getPostCount();
        if(count % POST_SIZE == 0){
            return getPostCount() / POST_SIZE;
        }else {
            return getPostCount() / POST_SIZE + 1;
        }

    }

    //Create
    public void insertPost(Long userId, PostInsertDTO postInsertDTO){
        log.info("게시글 데이터 변환 및 저장");
        Post post = postConverter.toPost(userId, postInsertDTO);
        postMapper.insertPost(post);
        log.info("게시글 내용 데이터 변환 및 저장");
        PostContent postContent = postConverter.toPostContent(post, postInsertDTO);
        postContentMapper.insertContent(postContent);
    }

    //Read
    public Post findByPostId(long postId){
        return RequireHelper.requireNonNull(
                postMapper.findByPostId(postId), new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public List<Post> findByUserId(long userId){
        return RequireHelper.requireNonEmpty(
                postMapper.findByUserId(userId), new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public List<Post> findByNotice(){
        return postMapper.findNotice();
    }

    public List<Post> findByPost(int page){
        return postMapper.findPost(POST_SIZE, POST_SIZE*(page-1));
    }

    public PostDetailDBDTO getPostDetail(long postId){
        return RequireHelper.requireNonNull(
                postDetailMapper.findPostDetailById(postId), new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public List<PostResponseDTO> findSearchPosts(String keyword, int page){
        return RequireHelper.requireNonEmpty(
                postDetailMapper.findSearchPosts(keyword, POST_SIZE, POST_SIZE*(page-1)),
                new BaseException(PostErrorCode.POST_NOT_FOUND));
    }

    public long getPostCount(){
        return postMapper.countPosts();
    }

    public long getSearchedPostCount(String keyword){
        return postDetailMapper.countSearchPosts(keyword);
    }

    //Update
    public void updatePost(PostUpdateDTO postUpdateDTO){
        Post oldPost = postMapper.findByPostId(postUpdateDTO.getPostId());
        Post newPost = postConverter.toUpdatePost(oldPost, postUpdateDTO);
        postMapper.updatePost(newPost);
        postContentMapper.updateContent(postConverter.toPostContent(newPost, postUpdateDTO));
    }

    //Delete
    public void deletePost(long postId){
        postMapper.deleteByPostId(postId);
    }

}
