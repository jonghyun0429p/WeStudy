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
    void ensurePostLikeCount(long postId);
    void ensureCommentLikeCount(long commentId);
    void minusLikeCommentCount(long commentId);
    void minusLikePostCount(long postId);
    boolean isPostLiked(long postId, long userId);
    boolean isCommentLiked(long commetnId, long userId);
    int findPostLikeCount(long postId);
    int findCommentLikeCount(long comment);
}
