package com.master.socialmedia.serviceimpl;

import com.master.socialmedia.dto.PostDTO;
import com.master.socialmedia.entity.Comment;
import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;
import com.master.socialmedia.exception.*;
import com.master.socialmedia.repository.PostRepository;
import com.master.socialmedia.repository.UserRepository;
import com.master.socialmedia.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_FOUND = "Post not found with id : ";
    private static final String USER_NOT_FOUND = "User not found with id : ";

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostDTO createPost(Post post, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);

        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        if (post.getCaption() == null || post.getCaption().isBlank()) {
            throw new UserOperationException("Post caption cannot be empty.");
        }

        post.setUser(user);
        post.setCreatedAt(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        post.setStatus(PostStatus.PUBLIC);
        post.setDeleted(false);
        post.setReported(false);
        post.setReportCount(0);

        // Avoid overwriting likes, saves, and comments if they are not meant to be set initially
        post.setLikedBy(new HashSet<>());
        post.setSavedBy(new HashSet<>());
        post.setComments(new ArrayList<>());

        Post savedPost = postRepository.save(post);
        return new PostDTO(savedPost);
    }

    @Override
    public List<PostDTO> getPostsByAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);

        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        List<Post> userPosts = postRepository.findByUser(user);

        if (userPosts.isEmpty()) {
            throw new ResourceNotFoundException("No posts found for user: " + currentUsername);
        }

        return userPosts.stream().map(PostDTO::new).toList();
    }


    @Override
    public List<PostDTO> getAllPublicPosts() {
        return postRepository.findByStatus(PostStatus.PUBLIC)
                .stream().map(PostDTO::new).toList();
    }

    @Override
    public List<PostDTO> getPostsByUser(Integer userId) {
        List<Post> userPosts = postRepository.findByUserIdAndStatus(userId, PostStatus.PUBLIC);
        return userPosts.stream().map(PostDTO::new).toList();
    }

    @Override
    public PostDTO updatePost(Integer postId, Post updatedPost, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUserName(currentUsername);
        if (currentUser == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (!existingPost.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not allowed to update this post");
        }

        // Update only the provided fields
        Optional.ofNullable(updatedPost.getCaption()).ifPresent(existingPost::setCaption);
        Optional.ofNullable(updatedPost.getImageUrl()).ifPresent(existingPost::setImageUrl);
        Optional.ofNullable(updatedPost.getVideoUrl()).ifPresent(existingPost::setVideoUrl);
        Optional.ofNullable(updatedPost.getStatus()).ifPresent(existingPost::setStatus);
        Optional.ofNullable(updatedPost.getLocation()).ifPresent(existingPost::setLocation);

        existingPost.setUpdatedAt(LocalDateTime.now());

        return new PostDTO(postRepository.save(existingPost));
    }


    @Override
    @Transactional
    public void deletePost(Integer postId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);
        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedActionException("You are not allowed to delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public PostDTO toggleLikePost(Integer postId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUserName(currentUsername);
        if (currentUser == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));

        if (post.getLikedBy().contains(currentUser)) {
            post.getLikedBy().remove(currentUser);
        } else {
            post.getLikedBy().add(currentUser);
        }

        Post savedPost = postRepository.save(post);
        return new PostDTO(savedPost);
    }

    @Override
    @Transactional
    public PostDTO toggleSavePost(Integer postId, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUserName(currentUsername);
        if (currentUser == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (post.getSavedBy().contains(currentUser)) {
            post.getSavedBy().remove(currentUser);
        } else {
            post.getSavedBy().add(currentUser);
        }

        Post savedPost = postRepository.save(post);
        return new PostDTO(savedPost);
    }


    @Override
    @Transactional
    public PostDTO addComment(Integer postId, String commentText, Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);
        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postId));


        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        post.getComments().add(comment);
        Post savedPost = postRepository.save(post);

        return new PostDTO(savedPost);
    }


    @Override
    public List<String> getCommentTexts(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));
        return post.getComments().stream()
                .map(Comment::getText)
                .toList();
    }

    @Override
    public List<PostDTO> getSavedPosts(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Invalid or missing authentication");
        }

        String currentUsername = authentication.getName();
        User user = userRepository.findByUserName(currentUsername);
        if (user == null) {
            throw new UserNotFoundException("Authenticated user not found.");
        }

        return postRepository.findAll().stream()
                .filter(post -> post.getSavedBy().contains(user))
                .map(PostDTO::new)
                .toList();
    }


    @Override
    public int getLikeCount(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));
        return post.getLikedBy().size();
    }

    @Override
    public int getCommentCount(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));
        return post.getComments().size();
    }

    @Override
    public List<PostDTO> searchPosts(String keyword) {
        return postRepository.findByCaptionContainingIgnoreCase(keyword)
                .stream().map(PostDTO::new).toList();
    }

    @Override
    public PostDTO changePostStatus(Integer postId, Integer userId, PostStatus newStatus) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));
        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("Only owner can change post status");
        }
        post.setStatus(newStatus);
        return new PostDTO(postRepository.save(post));
    }

    @Override
    public List<PostDTO> getPostsForUser(User viewer, Integer ownerId) {
        if (viewer == null) {
            throw new UnauthorizedActionException("Viewer information is required.");
        }

        List<Post> posts;
        if (viewer.getId().equals(ownerId)) {
            posts = postRepository.findByUser_IdAndStatusNot(ownerId, PostStatus.DELETED);
        } else {
            posts = postRepository.findByUser_IdAndStatusIn(
                    ownerId, List.of(PostStatus.PUBLIC, PostStatus.FRIENDS_ONLY)
            );
        }
        return posts.stream().map(PostDTO::new).toList();
    }
}