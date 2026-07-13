package com.westudy.bookmark.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookmarkMapper {
    int isBookmarked(@Param("postId") long postId, @Param("userId") long userId);
    void insertBookmark(@Param("postId") long postId, @Param("userId") long userId);
    void deleteBookmark(@Param("postId") long postId, @Param("userId") long userId);
}
