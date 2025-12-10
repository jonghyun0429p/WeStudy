package com.westudy.post.mapper;

import com.westudy.post.entity.Post;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PostMapper {
    Post findByPostId(long Id);
    List<Post> findByUserId(long userId);
    List<Post> findNotice();
    List<Post> findPost(int size, int offset);
    long findUserIdByPostId(long id);
    long countPosts();
    void insertPost(Post post);
    void updatePost(Post post);
    void deleteByPostId(long id);
}
