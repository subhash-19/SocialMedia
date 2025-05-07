package com.master.socialmedia.serviceimpl;

import com.master.socialmedia.entity.User;
import com.master.socialmedia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with email: " + identifier);
            }
        } else {
            user = userRepository.findByUserName(identifier);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with username : " + identifier);
            }
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .build();
    }
}
