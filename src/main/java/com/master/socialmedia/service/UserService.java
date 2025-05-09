package com.master.socialmedia.service;

import com.master.socialmedia.dto.UserDTO;
import com.master.socialmedia.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO findUserByUserName(Authentication authentication);

    String registerUser(User user);

    String signIn(String identifier, String rawPassword);

    UserDTO findUserById(Integer userId);

    UserDTO findUserByEmail(String email);

    UserDTO updateUser(User user, Authentication authentication);

    UserDTO followUser(Authentication authentication, Integer userId2);

    List<UserDTO> searchUser(String query);

    void deleteUser(Integer userId);

}
