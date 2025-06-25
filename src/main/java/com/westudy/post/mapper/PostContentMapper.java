package com.westudy.post.mapper;

import com.westudy.post.entity.PostContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostContentMapper {
    void insertContent(PostContent postContent);
    PostContent findByPostId(long id);
    void updateContent(PostContent postContent);
    void deleteByPostId(long id);
}
