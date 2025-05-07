package com.master.socialmedia.dto;

import com.master.socialmedia.entity.User;
import com.master.socialmedia.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Gender gender;
    private int followerCount;
    private int followingCount;

    public UserDTO(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.followerCount = user.getFollowers() != null ? user.getFollowers().size() : 0;
        this.followingCount = user.getFollowings() != null ? user.getFollowings().size() : 0;
    }
}


