package com.master.socialmedia.controller;

import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;
import com.master.socialmedia.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post, @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.createPost(post, userId));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping("/public")
    public ResponseEntity<List<Post>> getAllPublicPosts() {
        return ResponseEntity.ok(postService.getAllPublicPosts());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(postService.getPostsByUser(userId));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Integer postId,
                                           @RequestBody Post updatedPost,
                                           @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.updatePost(postId, updatedPost, userId));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/like/{postId}")
    public ResponseEntity<Post> likePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.likePost(postId, userId));
    }

    @PutMapping("/unlike/{postId}")
    public ResponseEntity<Post> unlikePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.unlikePost(postId, userId));
    }

    @PutMapping("/save/{postId}")
    public ResponseEntity<Post> savePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.savePost(postId, userId));
    }

    @PutMapping("/unsave/{postId}")
    public ResponseEntity<Post> unsavePost(@PathVariable Integer postId, @RequestParam Integer userId) {
        return ResponseEntity.ok(postService.unsavePost(postId, userId));
    }

    @PostMapping("/comment/{postId}")
    public ResponseEntity<Post> addComment(@PathVariable Integer postId,
                                           @RequestParam Integer userId,
                                           @RequestParam String comment) {
        return ResponseEntity.ok(postService.addComment(postId, userId, comment));
    }

    @GetMapping("/comments/{postId}")
    public ResponseEntity<List<String>> getCommentTexts(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getCommentTexts(postId));
    }

    @GetMapping("/saved/{userId}")
    public ResponseEntity<List<Post>> getSavedPosts(@PathVariable Integer userId) {
        return ResponseEntity.ok(postService.getSavedPosts(userId));
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
    public ResponseEntity<List<Post>> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    @PutMapping("/status/{postId}")
    public ResponseEntity<Post> changePostStatus(@PathVariable Integer postId,
                                                 @RequestParam Integer userId,
                                                 @RequestParam PostStatus status) {
        return ResponseEntity.ok(postService.changePostStatus(postId, userId, status));
    }

    @GetMapping("/viewable/{ownerId}")
    public ResponseEntity<List<Post>> getViewablePosts(@PathVariable Integer ownerId,
                                                       @RequestParam Integer viewerId) {
        User viewer = new User();
        viewer.setId(viewerId);
        return ResponseEntity.ok(postService.getPostsForUser(viewer, ownerId));
    }
}