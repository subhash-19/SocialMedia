package com.master.socialmedia.repository;

import com.master.socialmedia.entity.Post;
import com.master.socialmedia.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByStatus(PostStatus status);

    List<Post> findByUserId(Integer userId);

    List<Post> findByCaptionContainingIgnoreCase(String keyword);

    List<Post> findByUserIdAndStatusNot(Integer userId, PostStatus status);

    List<Post> findByUserIdAndStatusIn(Integer userId, List<PostStatus> statuses);

}
