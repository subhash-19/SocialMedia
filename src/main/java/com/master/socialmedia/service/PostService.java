package com.master.socialmedia.service;

import com.master.socialmedia.dto.PostDTO;
import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;

import java.util.List;

public interface PostService {

    PostDTO createPost(Post post, Integer userId);

    PostDTO getPostById(Integer postId);

    List<PostDTO> getAllPublicPosts();

    List<PostDTO> getPostsByUser(Integer userId);

    PostDTO updatePost(Integer postId, Post updatedPost, Integer userId);

    void deletePost(Integer postId, Integer userId); // soft delete

    PostDTO toggleLikePost(Integer postId, Integer userId);

    PostDTO savePost(Integer postId, Integer userId);

    PostDTO addComment(Integer postId, Integer userId, String commentText);

    List<String> getCommentTexts(Integer postId); // for simple display

    List<PostDTO> getSavedPosts(Integer userId);

    int getLikeCount(Integer postId);

    int getCommentCount(Integer postId);

    List<PostDTO> getPostsForUser(User viewer, Integer ownerId);

    List<PostDTO> searchPosts(String keyword);

    PostDTO changePostStatus(Integer postId, Integer userId, PostStatus newStatus);
}

