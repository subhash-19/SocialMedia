package com.master.socialmedia.service;

import com.master.socialmedia.dto.PostDTO;
import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PostService {

    PostDTO createPost(Post post, Authentication authentication);

    List<PostDTO> getPostsByAuthenticatedUser(Authentication authentication);

    List<PostDTO> getAllPublicPosts();

    List<PostDTO> getPostsByUser(Integer userId);

    PostDTO updatePost(Integer postId, Post updatedPost, Authentication authentication);

    void deletePost(Integer postId, Authentication authentication);

    PostDTO toggleLikePost(Integer postId, Authentication authentication);

    PostDTO toggleSavePost(Integer postId, Authentication authentication);

    PostDTO addComment(Integer postId, String commentText, Authentication authentication);

    List<String> getCommentTexts(Integer postId); // for simple display

    List<PostDTO> getSavedPosts(Authentication authentication);

    int getLikeCount(Integer postId);

    int getCommentCount(Integer postId);

    List<PostDTO> getPostsForUser(User viewer, Integer ownerId);

    List<PostDTO> searchPosts(String keyword);

    PostDTO changePostStatus(Integer postId, Integer userId, PostStatus newStatus);
}

