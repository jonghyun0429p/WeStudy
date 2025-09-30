package com.westudy.like.service;

import com.westudy.global.exception.BaseException;
import com.westudy.like.enums.LikeErrorCode;
import com.westudy.like.mapper.LikeMapper;
import com.westudy.security.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

@Slf4j
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
        likeMapper.ensurePostLikeCount(postId);
        likeMapper.insertLikePost(postId, SecurityUtil.getCurrentUserId());
        likeMapper.addLikePostCount(postId);
    }

    public void insertCommentLike(long commenId){
        likeMapper.ensureCommentLikeCount(commenId);
        likeMapper.insertLikeComment(commenId, SecurityUtil.getCurrentUserId());
        likeMapper.addLikePostCount(commenId);
    }

    //find
    public int getPostLikeCount(long postId){
        return likeMapper.findPostLikeCount(postId);
    }

    public boolean checkPostLike(long postId){
        return likeMapper.isPostLiked(postId, SecurityUtil.getCurrentUserId());
    }

    public boolean checkCommentLike(long commentId){
        return likeMapper.isCommentLiked(commentId, SecurityUtil.getCurrentUserId());
    }

    public int getCommentCount(long commentId){
        return likeMapper.findCommentLikeCount(commentId);
    }

    //delete
    public void minusPostLike(long postId){
        likeMapper.deleteLikePost(postId, SecurityUtil.getCurrentUserId());
        likeMapper.minusLikePostCount(postId);
    }

    public void minusCommentLike(long commentId){
        likeMapper.deleteLikeComment(commentId, SecurityUtil.getCurrentUserId());
        likeMapper.minusLikeCommentCount(commentId);
    }
}
