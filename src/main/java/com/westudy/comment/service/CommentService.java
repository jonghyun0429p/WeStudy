package com.westudy.comment.service;

import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentResponseDTO;
import com.westudy.comment.dto.CommentUpdateDTO;
import com.westudy.comment.entity.Comment;
import com.westudy.comment.enums.CommentErrorCode;
import com.westudy.comment.mapper.CommentMapper;
import com.westudy.global.exception.BaseException;
import com.westudy.security.util.SecurityUtil;
import com.westudy.like.service.LikeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final LikeService likeService;

    public CommentService(CommentMapper commentMapper, LikeService likeService) {
        this.commentMapper = commentMapper;
        this.likeService = likeService;
    }

    public void isWriter(long commentId){
        long id = SecurityUtil.getCurrentUserId();
        long commentUserId = commentMapper.findUserIdByCommentId(commentId);
        if(id != commentUserId){
            throw new BaseException(CommentErrorCode.COMMENT_NOT_WRITER);
        }
    }

    //create
    public void insertComment(CommentInsertDTO commentInsertDTO){
        commentInsertDTO.setUserId(SecurityUtil.getCurrentUserId());
        commentMapper.insertComment(commentInsertDTO);
    }

    //Read
    public List<Comment> findCommentById(long id){
        return commentMapper.findCommentById(id);
    }

    public List<CommentResponseDTO> getCommentsByPostId(long postId) {
        Long currentUserId = SecurityUtil.resolveCurrentUserIdSafely();
        List<CommentResponseDTO> comments = commentMapper.findCommentsByPostId(postId);
        for (CommentResponseDTO comment : comments) {
            comment.setWriter(currentUserId != null && currentUserId.equals(comment.getUserId()));
            comment.setLikeCount(likeService.getCommentCount(comment.getCommentId()));
            if (currentUserId != null) {
                comment.setLiked(likeService.checkCommentLike(comment.getCommentId()));
            } else {
                comment.setLiked(false);
            }
        }
        return comments;
    }

    //Update
    public void updateComment(CommentUpdateDTO commentUpdateDTO){
        isWriter(commentUpdateDTO.getId());
        commentMapper.updateComment(commentUpdateDTO);
    }

    //Delete
    public void deleteComment(long id){
        isWriter(id);
        commentMapper.deleteComment(id);
    }

}
