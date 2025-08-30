package com.westudy.like.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper {
    void insertLikeComment(long commentId, long userId);
    void insertLikePost(long postId, long userId);
    void addLikeCommentCount(long commentId);
    void addLikePostCount(long postId);
    void deleteLikeComment(long commentId, long userId);
    void deleteLikePost(long commentId, long userId);
    void minusLikeCommentCount(long commentId);
    void minusLikePostCount(long postId);
}
