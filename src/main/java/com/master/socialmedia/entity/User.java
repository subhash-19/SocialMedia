package com.master.socialmedia.entity;

import com.master.socialmedia.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "Username can only contain lowercase letters, numbers, and underscores")
    private String userName;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character"
    )
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> followers = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Integer> followings = new HashSet<>();

    @ManyToMany(mappedBy = "likedBy")
    private Set<Post> likedPosts = new HashSet<>();

    @ManyToMany(mappedBy = "savedBy")
    private Set<Post> savedPosts = new HashSet<>();
}