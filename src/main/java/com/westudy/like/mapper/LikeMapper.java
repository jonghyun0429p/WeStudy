package com.westudy.like.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LikeMapper {
    void insertLikeComment(@Param("commentId") long commentId, @Param("userId") long userId);
    void insertLikePost(@Param("postId") long postId, @Param("userId") long userId);
    void addLikeCommentCount(@Param("commentId") long commentId);
    void addLikePostCount(@Param("postId") long postId);
    void deleteLikeComment(@Param("commentId") long commentId, @Param("userId") long userId);
    void deleteLikePost(@Param("postId") long postId, @Param("userId") long userId);
    void ensurePostLikeCount(@Param("postId") long postId);
    void ensureCommentLikeCount(@Param("commentId") long commentId);
    void minusLikeCommentCount(@Param("commentId") long commentId);
    void minusLikePostCount(@Param("postId") long postId);
    boolean isPostLiked(@Param("postId") long postId, @Param("userId") long userId);
    boolean isCommentLiked(@Param("commentId") long commentId, @Param("userId") long userId);
    Integer findPostLikeCount(@Param("postId") long postId);
    Integer findCommentLikeCount(@Param("commentId") long commentId);
}
