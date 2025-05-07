package com.master.socialmedia.entity;

import com.master.socialmedia.enums.PostStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_table")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String caption;

    private String imageUrl;
    private String videoUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private String location;

    @ManyToOne
    private User user;

    @ElementCollection
    private Set<Integer> likedBy = new HashSet<>();

    @ElementCollection
    private Set<Integer> savedBy = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    private boolean isDeleted = false;
    private boolean isReported = false;
    private int reportCount = 0;
}
