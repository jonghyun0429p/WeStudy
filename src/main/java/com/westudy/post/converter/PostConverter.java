package com.westudy.post.converter;

import com.westudy.post.dto.PostInsertDTO;
import com.westudy.post.dto.PostResponseDTO;
import com.westudy.post.entity.Post;
import com.westudy.post.entity.PostContent;
import com.westudy.post.enums.PostCategory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostConverter {
//
//    public PostInsertDTO toPostInsertDTO(Post post, String content){
//        PostInsertDTO dto = new PostInsertDTO();
//        dto.setPostCategory(post.getCategory());
//        dto.setTitle(post.getTitle());
//        dto.setContent(content);
//
//        return dto;
//    }

    public Post toPost(PostInsertDTO postInsertDTO){
        String content = postInsertDTO.getContent();
        String summary = content.substring(0, Math.min(content.length(), 20));

        return Post.builder()
                .views(0)
                .isNotice(postInsertDTO.isNotice())
                .category(postInsertDTO.getPostCategory())
                .title(postInsertDTO.getTitle())
                .summary(summary)
                .createAt(LocalDateTime.now())
                .deleteAt(null)
                .build();
    }

    public PostContent toPostContent(Post post, PostInsertDTO postInsertDTO){
        return PostContent.builder()
                .postId(post.getId())
                .content(postInsertDTO.getContent())
                .modifyAt(LocalDateTime.now())
                .build();
    }


    public PostResponseDTO toResponseDTO(Post post){
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getId());
        dto.setPostCategory(post.getCategory());
        dto.setSummary(post.getSummary());
        dto.setTitle(post.getTitle());
        dto.setViews(post.getViews());

        return dto;
    }
}
