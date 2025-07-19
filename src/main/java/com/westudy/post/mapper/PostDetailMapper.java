package com.westudy.post.mapper;

import com.westudy.post.dto.PostDetailDBDTO;
import com.westudy.post.dto.PostResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostDetailMapper {
    PostDetailDBDTO findPostDetailById(@Param("postId") long postId);
    List<PostResponseDTO> findSearchPosts(String keyword, int size, int offset);
    long countSearchPosts(String keyword);
}
