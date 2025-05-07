package com.master.socialmedia.serviceimpl;

import com.master.socialmedia.dto.PostDTO;
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
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_FOUND = "Post not found with id : ";

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public PostDTO createPost(Post post, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post.setStatus(PostStatus.PUBLIC);

        Post savedPost = postRepository.save(post);
        return new PostDTO(savedPost);
    }

    @Override
    public PostDTO getPostById(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));
        return new PostDTO(post);
    }

    @Override
    public List<PostDTO> getAllPublicPosts() {
        return postRepository.findByStatus(PostStatus.PUBLIC)
                .stream().map(PostDTO::new).toList();
    }

    @Override
    public List<PostDTO> getPostsByUser(Integer userId) {
        return postRepository.findByUser_Id(userId)
                .stream().map(PostDTO::new).toList();
    }

    @Override
    public PostDTO updatePost(Integer postId, Post updatedPost, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You are not allowed to update this post");
        }

        if (updatedPost.getCaption() != null) {
            post.setCaption(updatedPost.getCaption());
        }

        if (updatedPost.getImageUrl() != null) {
            post.setImageUrl(updatedPost.getImageUrl());
        }

        if (updatedPost.getVideoUrl() != null) {
            post.setVideoUrl(updatedPost.getVideoUrl());
        }

        if (updatedPost.getStatus() != null) {
            post.setStatus(updatedPost.getStatus());
        }

        if (updatedPost.getLocation() != null) {
            post.setLocation(updatedPost.getLocation());
        }

        post.setUpdatedAt(LocalDateTime.now());

        return new PostDTO(postRepository.save(post));
    }

    @Override
    @Transactional
    public void deletePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (post.isDeleted()) {
            throw new ResourceNotFoundException("Post was already deleted");
        }

        if (!post.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You are not allowed to delete this post");
        }

        post.setDeleted(true);
        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);
    }

    @Override
    public PostDTO toggleLikePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (post.getLikedBy().contains(userId)) {
            post.getLikedBy().remove(userId);
        } else {
            post.getLikedBy().add(userId);
        }

        return new PostDTO(postRepository.save(post));
    }

    @Override
    public PostDTO savePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));

        if (post.getSavedBy().contains(userId)) {
            post.getSavedBy().remove(userId);
        } else {
            post.getSavedBy().add(userId);
        }

        return new PostDTO(postRepository.save(post));
    }

    @Override
    public PostDTO addComment(Integer postId, Integer userId, String commentText) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND + postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        post.getComments().add(comment);
        return new PostDTO(postRepository.save(post));
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
    public List<PostDTO> getSavedPosts(Integer userId) {
        return postRepository.findAll().stream()
                .filter(post -> post.getSavedBy().contains(userId))
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