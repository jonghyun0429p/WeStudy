package com.westudy.post.converter;

import com.westudy.post.dto.*;
import com.westudy.post.entity.Post;
import com.westudy.post.entity.PostContent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public Post toPost(long userId, PostInsertDTO postInsertDTO){
        String content = postInsertDTO.getContent();
        String summary = content.substring(0, Math.min(content.length(), 20));

        return Post.builder()
                .userId(userId)
                .views(0)
                .isNotice(postInsertDTO.isNotice())
                .category(postInsertDTO.getPostCategory())
                .title(postInsertDTO.getTitle())
                .summary(summary)
                .createAt(LocalDateTime.now())
                .deleteAt(null)
                .build();
    }

    public Post toUpdatePost(Post oldPost, PostUpdateDTO postUpdateDTO){
        String content = postUpdateDTO.getContent();
        String summary = content.substring(0, Math.min(content.length(), 20));

        return Post.builder()
                .id(oldPost.getId())
                .category(oldPost.getCategory())
                .title(postUpdateDTO.getTitle())
                .summary(summary)
                .build();
    }

    public PostContent toPostContent(Post post, PostInsertDTO postInsertDTO){
        return PostContent.builder()
                .postId(post.getId())
                .content(postInsertDTO.getContent())
                .modifyAt(LocalDateTime.now())
                .build();
    }

    public PostContent toPostContent(Post post, PostUpdateDTO postUpdateDTO){
        return PostContent.builder()
                .postId(post.getId())
                .content(postUpdateDTO.getContent())
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

    public List<PostResponseDTO> toResponseDTOList(List<Post> posts){
        return posts.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PostDetailResponseDTO toDetailResponseDTO(PostDetailDBDTO postDetailDBDTO, String nickname){
        PostDetailResponseDTO dto = new PostDetailResponseDTO();
        dto.setPostId(postDetailDBDTO.getPostId());
        dto.setTitle(postDetailDBDTO.getTitle());
        dto.setContent(postDetailDBDTO.getContent());
        dto.setCategory(postDetailDBDTO.getCategory());
        dto.setViews(postDetailDBDTO.getViews());
        dto.setWriterNickname(postDetailDBDTO.getNickname());
        dto.setWriter(nickname.equals(postDetailDBDTO.getNickname()));
        dto.setCreatedAt(postDetailDBDTO.getCreatedAt());
        dto.setModifiedAt(postDetailDBDTO.getModifiedAt());

        return dto;
    }
}
