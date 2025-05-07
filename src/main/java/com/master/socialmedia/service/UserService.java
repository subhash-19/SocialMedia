package com.master.socialmedia.service;

import com.master.socialmedia.dto.UserDTO;
import com.master.socialmedia.entity.User;

import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsers();

    UserDTO registerUser(User user);

    UserDTO findUserById(Integer userId);

    UserDTO findUserByEmail(String email);

    UserDTO updateUser(User user, Integer userId);

    UserDTO followUser(Integer userId1, Integer userId2);

    List<UserDTO> searchUser(String query);

    void deleteUser(Integer userId);

}
