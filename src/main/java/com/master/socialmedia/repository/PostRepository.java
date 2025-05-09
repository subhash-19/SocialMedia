package com.master.socialmedia.repository;

import com.master.socialmedia.entity.Post;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findByStatus(PostStatus status);

    List<Post> findByUser_Id(Integer userId);

    List<Post> findByCaptionContainingIgnoreCase(String keyword);

    List<Post> findByUser_IdAndStatusNot(Integer userId, PostStatus status);

    List<Post> findByUser_IdAndStatusIn(Integer userId, List<PostStatus> statuses);

    List<Post> findByUserIdAndStatus(Integer userId, PostStatus status);

    List<Post> findByUser(User user);
}

