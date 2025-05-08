package com.master.socialmedia.serviceimpl;

import com.master.socialmedia.dto.UserDTO;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.exception.*;
import com.master.socialmedia.repository.UserRepository;
import com.master.socialmedia.service.UserService;
import com.master.socialmedia.utils.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_ID_MSG = "User not found with id: ";
    private static final String EMAIL_ALREADY_EXISTS_MSG = "Email already exists: ";

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::new)
                .toList();
    }

    @Override
    public UserDTO findUserByUserName(String username) {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new UserDTO(user);
    }

    @Override
    public String registerUser(User user) {

        if (userRepository.existsByUserName(user.getUserName())) {
            throw new UsernameAlreadyExistsException("Username already exists: " + user.getUserName());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS_MSG + user.getEmail());
        }

        User newUser = new User();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setUserName(user.getUserName());
        newUser.setGender(user.getGender());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(newUser);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));
        return jwtUtil.generateToken(user.getUserName());
    }

    @Override
    public String signIn(String identifier, String rawPassword) {
        User user;

        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + identifier);
            }
        } else {
            user = userRepository.findByUserName(identifier);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + identifier);
            }
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUserName(), rawPassword));

        return jwtUtil.generateToken(user.getUserName());
    }

    @Override
    public UserDTO findUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(UserDTO::new)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID_MSG + userId));
    }

    @Override
    public UserDTO findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(USER_NOT_FOUND_ID_MSG + email);
        }
        return new UserDTO(user);
    }

    @Override
    public UserDTO followUser(Integer userId1, Integer userId2) {
        if (userId1.equals(userId2)) {
            throw new UserOperationException("User cannot follow themselves.");
        }

        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId1 + " not found"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId2 + " not found"));

        try {
            user2.getFollowers().add(user1.getId());
            user1.getFollowings().add(user2.getId());

            userRepository.save(user1);
            userRepository.save(user2);

            return new UserDTO(user1);
        } catch (Exception e) {
            throw new UserOperationException("Failed to follow user: " + e.getMessage());
        }
    }

    @Override
    public UserDTO updateUser(User user, Integer userId) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID_MSG + userId));

        try {
            if (user.getFirstName() != null) {
                existingUser.setFirstName(user.getFirstName());
            }

            if (user.getLastName() != null) {
                existingUser.setLastName(user.getLastName());
            }

            if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
                User userWithEmail = userRepository.findByEmail(user.getEmail());
                if (userWithEmail != null && !userWithEmail.getId().equals(userId)) {
                    throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS_MSG + user.getEmail());
                }
                existingUser.setEmail(user.getEmail());
            }

            return new UserDTO(userRepository.save(existingUser));
        } catch (Exception e) {
            throw new UserOperationException("Failed to update user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_ID_MSG + userId));

        userRepository.delete(user);
    }

    @Override
    public List<UserDTO> searchUser(String query) {
        return userRepository.searchUser(query)
                .stream()
                .map(UserDTO::new)
                .toList();
    }
}