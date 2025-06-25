package com.westudy.post.mapper;

import com.westudy.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    Post findByPostId(long Id);
    Post findByUserId(long userId);
    void insertPost(Post post);
    void updatePost(Post post);
    void deleteByPostId(long id);
}
