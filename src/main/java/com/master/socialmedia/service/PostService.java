package com.master.socialmedia.service;

import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;

import java.util.List;

public interface PostService {

    Post createPost(Post post, Integer userId);

    Post getPostById(Integer postId);

    List<Post> getAllPublicPosts();

    List<Post> getPostsByUser(Integer userId);

    Post updatePost(Integer postId, Post updatedPost, Integer userId);

    void deletePost(Integer postId, Integer userId); // soft delete

    Post likePost(Integer postId, Integer userId);

    Post unlikePost(Integer postId, Integer userId);

    Post savePost(Integer postId, Integer userId);

    Post unsavePost(Integer postId, Integer userId);

    Post addComment(Integer postId, Integer userId, String commentText);

    List<String> getCommentTexts(Integer postId); // for simple display

    List<Post> getSavedPosts(Integer userId);

    int getLikeCount(Integer postId);

    int getCommentCount(Integer postId);

    List<Post> getPostsForUser(User viewer, Integer ownerId);

    List<Post> searchPosts(String keyword);

    Post changePostStatus(Integer postId, Integer userId, PostStatus newStatus);
}
