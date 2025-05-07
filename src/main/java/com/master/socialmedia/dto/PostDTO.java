package com.master.socialmedia.dto;

import com.master.socialmedia.entity.Post;
import com.master.socialmedia.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Integer id;
    private String caption;
    private String imageUrl;
    private String videoUrl;
    private LocalDateTime createdAt;
    private String location;
    private PostStatus status;
    private UserDTO user;
    private int likeCount;
    private int saveCount;
    private int commentCount;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.imageUrl = post.getImageUrl();
        this.videoUrl = post.getVideoUrl();
        this.createdAt = post.getCreatedAt();
        this.location = post.getLocation();
        this.status = post.getStatus();
        this.user = new UserDTO(post.getUser());
        this.likeCount = post.getLikedBy() != null ? post.getLikedBy().size() : 0;
        this.saveCount = post.getSavedBy() != null ? post.getSavedBy().size() : 0;
        this.commentCount = post.getComments() != null ? post.getComments().size() : 0;
    }
}

