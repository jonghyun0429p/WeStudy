package com.westudy.like.service;

import com.westudy.global.exception.BaseException;
import com.westudy.like.enums.LikeErrorCode;
import com.westudy.like.mapper.LikeMapper;
import com.westudy.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LikeService {

    private final LikeMapper likeMapper;

    public LikeService(LikeMapper likeMapper) {
        this.likeMapper = likeMapper;
    }

    public void likePost(long postId){
        try {
            insertPostLike(postId);
        }catch (DataIntegrityViolationException e){
            log.error("없는 commentId로 좋아요 시도");
            throw new BaseException(LikeErrorCode.LIKE_NOT_EXIST_POST_ID);
        }
    }

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

    public void notlikePostLike(long postId){
        minusPostLike(postId);
    }

    public void notlikeCommentLike(long commentId){
        minusCommentLike(commentId);
    }

    //create
    public void insertPostLike(long postId){
        long userId = SecurityUtil.getCurrentUserId();
        likeMapper.ensurePostLikeCount(postId);
        if (!likeMapper.isPostLiked(postId, userId)) {
            likeMapper.insertLikePost(postId, userId);
            likeMapper.addLikePostCount(postId);
        }
    }

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
    public void minusPostLike(long postId){
        long userId = SecurityUtil.getCurrentUserId();
        if (likeMapper.isPostLiked(postId, userId)) {
            likeMapper.deleteLikePost(postId, userId);
            likeMapper.minusLikePostCount(postId);
        }
    }

    public void minusCommentLike(long commentId){
        long userId = SecurityUtil.getCurrentUserId();
        if (likeMapper.isCommentLiked(commentId, userId)) {
            likeMapper.deleteLikeComment(commentId, userId);
            likeMapper.minusLikeCommentCount(commentId);
        }
    }
}
