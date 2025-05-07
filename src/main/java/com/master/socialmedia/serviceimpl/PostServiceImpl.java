package com.master.socialmedia.serviceimpl;

import com.master.socialmedia.entity.Comment;
import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;
import com.master.socialmedia.exception.ResourceNotFoundException;
import com.master.socialmedia.exception.UnauthorizedActionException;
import com.master.socialmedia.repository.PostRepository;
import com.master.socialmedia.repository.UserRepository;
import com.master.socialmedia.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public Post createPost(Post post, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.PUBLIC);
        return postRepository.save(post);
    }

    @Override
    public Post getPostById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    @Override
    public List<Post> getAllPublicPosts() {
        return postRepository.findByStatus(PostStatus.PUBLIC);
    }

    @Override
    public List<Post> getPostsByUser(Integer userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    public Post updatePost(Integer postId, Post updatedPost, Integer userId) {
        Post post = getPostById(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You are not allowed to update this post");
        }

        post.setCaption(updatedPost.getCaption());
        post.setImageUrl(updatedPost.getImageUrl());
        post.setVideoUrl(updatedPost.getVideoUrl());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        Post post = getPostById(postId);

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You are not allowed to delete this post");
        }

        post.setDeleted(true);
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);
    }

    @Override
    public Post likePost(Integer postId, Integer userId) {
        Post post = getPostById(postId);
        if (post.getLikedBy().contains(userId)) {
            throw new IllegalStateException("Already liked this post");
        }
        post.getLikedBy().add(userId);
        return postRepository.save(post);
    }

    @Override
    public Post unlikePost(Integer postId, Integer userId) {
        Post post = getPostById(postId);
        if (!post.getLikedBy().contains(userId)) {
            throw new IllegalStateException("You haven't liked this post yet");
        }
        post.getLikedBy().remove(userId);
        return postRepository.save(post);
    }

    @Override
    public Post savePost(Integer postId, Integer userId) {
        Post post = getPostById(postId);
        post.getSavedBy().add(userId);
        return postRepository.save(post);
    }

    @Override
    public Post unsavePost(Integer postId, Integer userId) {
        Post post = getPostById(postId);
        post.getSavedBy().remove(userId);
        return postRepository.save(post);
    }

    @Override
    public Post addComment(Integer postId, Integer userId, String commentText) {
        Post post = getPostById(postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        post.getComments().add(comment); // cascade saves the comment
        return postRepository.save(post);
    }

    @Override
    public List<String> getCommentTexts(Integer postId) {
        Post post = getPostById(postId);
        return post.getComments().stream()
                .map(Comment::getText)
                .toList();
    }

    @Override
    public List<Post> getSavedPosts(Integer userId) {
        return postRepository.findAll().stream()
                .filter(post -> post.getSavedBy().contains(userId))
                .toList();
    }

    @Override
    public int getLikeCount(Integer postId) {
        return getPostById(postId).getLikedBy().size();
    }

    @Override
    public int getCommentCount(Integer postId) {
        return getPostById(postId).getComments().size();
    }

    @Override
    public List<Post> searchPosts(String keyword) {
        return postRepository.findByCaptionContainingIgnoreCase(keyword);
    }

    @Override
    public Post changePostStatus(Integer postId, Integer userId, PostStatus newStatus) {
        Post post = getPostById(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("Only owner can change post status");
        }
        post.setStatus(newStatus);
        return postRepository.save(post);
    }

    @Override
    public List<Post> getPostsForUser(User viewer, Integer ownerId) {
        if (viewer == null) {
            throw new UnauthorizedActionException("Viewer information is required.");
        }

        if (viewer.getId().equals(ownerId)) {
            return postRepository.findByUserIdAndStatusNot(ownerId, PostStatus.DELETED);
        } else {
            return postRepository.findByUserIdAndStatusIn(
                    ownerId, List.of(PostStatus.PUBLIC, PostStatus.FRIENDS_ONLY)
            );
        }
    }
}
