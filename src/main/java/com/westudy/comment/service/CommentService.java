package com.westudy.comment.service;

import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentUpdateDTO;
import com.westudy.comment.entity.Comment;
import com.westudy.comment.enums.CommentErrorCode;
import com.westudy.comment.mapper.CommentMapper;
import com.westudy.global.exception.BaseException;
import com.westudy.security.util.SecurityUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
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
