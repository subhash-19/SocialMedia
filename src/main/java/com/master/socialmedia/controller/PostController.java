package com.master.socialmedia.controller;

import com.master.socialmedia.dto.PostDTO;
import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;
import com.master.socialmedia.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createPost(@RequestBody Post post, Authentication authentication) {
        return ResponseEntity.ok(postService.createPost(post, authentication));
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getPostsOfCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(postService.getPostsByAuthenticatedUser(authentication));
    }

    @GetMapping("/public-post")
    public ResponseEntity<List<PostDTO>> getAllPublicPosts() {
        List<PostDTO> posts = postService.getAllPublicPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDTO>> getPostsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable Integer postId,
                                              @RequestBody Post updatedPost,
                                              Authentication authentication) {
        return ResponseEntity.ok(postService.updatePost(postId, updatedPost, authentication));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Integer postId,
                                             Authentication authentication) {
        postService.deletePost(postId, authentication);
        return ResponseEntity.ok("Post deleted successfully.");
    }

    @PostMapping("/like/{postId}")
    public ResponseEntity<PostDTO> toggleLikePost(@PathVariable Integer postId, Authentication authentication) {
        PostDTO updatedPost = postService.toggleLikePost(postId, authentication);
        return ResponseEntity.ok(updatedPost);
    }


    @PostMapping("/save/{postId}")
    public ResponseEntity<PostDTO> savePost(@PathVariable Integer postId, Authentication authentication) {
        PostDTO savedPost = postService.toggleSavePost(postId, authentication);
        return ResponseEntity.ok(savedPost);
    }


    @PostMapping("/comment/{postId}")
    public ResponseEntity<PostDTO> addComment(@PathVariable Integer postId,
                                              @RequestParam String commentText,
                                              Authentication authentication) {
        PostDTO updatedPost = postService.addComment(postId, commentText, authentication);
        return ResponseEntity.ok(updatedPost);
    }


    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<String>> getCommentTexts(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getCommentTexts(postId));
    }

    @GetMapping("/saved-posts")
    public ResponseEntity<List<PostDTO>> getSavedPosts(Authentication authentication) {
        List<PostDTO> savedPosts = postService.getSavedPosts(authentication);
        return ResponseEntity.ok(savedPosts);
    }


    @GetMapping("/likes/count/{postId}")
    public ResponseEntity<Integer> getLikeCount(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getLikeCount(postId));
    }

    @GetMapping("/comments/count/{postId}")
    public ResponseEntity<Integer> getCommentCount(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getCommentCount(postId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    @PutMapping("/status/{postId}")
    public ResponseEntity<PostDTO> changePostStatus(@PathVariable Integer postId,
                                                    @RequestParam Integer userId,
                                                    @RequestParam PostStatus status) {
        return ResponseEntity.ok(postService.changePostStatus(postId, userId, status));
    }

    @GetMapping("/viewable/{ownerId}")
    public ResponseEntity<List<PostDTO>> getViewablePosts(@PathVariable Integer ownerId,
                                                          @RequestParam Integer viewerId) {
        User viewer = new User();
        viewer.setId(viewerId);
        return ResponseEntity.ok(postService.getPostsForUser(viewer, ownerId));
    }
}