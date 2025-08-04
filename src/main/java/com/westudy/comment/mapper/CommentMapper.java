package com.westudy.comment.mapper;

import com.westudy.comment.dto.CommentInsertDTO;
import com.westudy.comment.dto.CommentUpdateDTO;
import com.westudy.comment.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    void insertComment(CommentInsertDTO commentInsertDTO);
    List<Comment> findCommentById(long id);
    long findUserIdByCommentId(long id);
    void updateComment(CommentUpdateDTO commentUpdateDTO);
    void deleteComment(long commentId);
}
