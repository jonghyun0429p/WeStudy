package com.westudy.like.service;

import com.westudy.global.exception.BaseException;
import com.westudy.like.enums.LikeErrorCode;
import com.westudy.like.mapper.LikeMapper;
import com.westudy.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LikeService {

    private final LikeMapper likeMapper;

    public LikeService(LikeMapper likeMapper) {
        this.likeMapper = likeMapper;
    }

    @Transactional
    public void likePost(long postId){
        try {
            insertPostLike(postId);
        }catch (DataIntegrityViolationException e){
            log.error("없는 commentId로 좋아요 시도");
            throw new BaseException(LikeErrorCode.LIKE_NOT_EXIST_POST_ID);
        }
    }

    @Transactional
    public void likeComment(long commentId){
        try{
            insertCommentLike(commentId);
        }catch (DataIntegrityViolationException e){
            log.error("없는 commentId로 좋아요 시도");
            throw new BaseException(LikeErrorCode.LIKE_NOT_EXIST_COMMENT_ID);
        }
    }

    public int getPostLikes(long postId){
        return getPostLikeCount(postId);
    }

    public int getCommetnLikes(long commentId){
        return getCommentCount(commentId);
    }

    @Transactional
    public void notlikePostLike(long postId){
        minusPostLike(postId);
    }

    @Transactional
    public void notlikeCommentLike(long commentId){
        minusCommentLike(commentId);
    }

    //create
    @Transactional
    public void insertPostLike(long postId){
        long userId = SecurityUtil.getCurrentUserId();
        likeMapper.ensurePostLikeCount(postId);
        if (!likeMapper.isPostLiked(postId, userId)) {
            likeMapper.insertLikePost(postId, userId);
            likeMapper.addLikePostCount(postId);
        }
    }

    @Transactional
    public void insertCommentLike(long commenId){
        long userId = SecurityUtil.getCurrentUserId();
        likeMapper.ensureCommentLikeCount(commenId);
        if (!likeMapper.isCommentLiked(commenId, userId)) {
            likeMapper.insertLikeComment(commenId, userId);
            likeMapper.addLikeCommentCount(commenId);
        }
    }

    //find
    public int getPostLikeCount(long postId){
        Integer count = likeMapper.findPostLikeCount(postId);
        return count == null ? 0 : count;
    }

    public boolean checkPostLike(long postId){
        return likeMapper.isPostLiked(postId, SecurityUtil.getCurrentUserId());
    }

    public boolean checkCommentLike(long commentId){
        return likeMapper.isCommentLiked(commentId, SecurityUtil.getCurrentUserId());
    }

    public int getCommentCount(long commentId){
        Integer count = likeMapper.findCommentLikeCount(commentId);
        return count == null ? 0 : count;
    }

    //delete
    @Transactional
    public void minusPostLike(long postId){
        long userId = SecurityUtil.getCurrentUserId();
        if (likeMapper.isPostLiked(postId, userId)) {
            likeMapper.deleteLikePost(postId, userId);
            likeMapper.minusLikePostCount(postId);
        }
    }

    @Transactional
    public void minusCommentLike(long commentId){
        long userId = SecurityUtil.getCurrentUserId();
        if (likeMapper.isCommentLiked(commentId, userId)) {
            likeMapper.deleteLikeComment(commentId, userId);
            likeMapper.minusLikeCommentCount(commentId);
        }
    }
}
