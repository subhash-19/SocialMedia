package com.master.socialmedia.controller;

import com.master.socialmedia.dto.UserDTO;
import com.master.socialmedia.entity.User;
import com.master.socialmedia.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class PublicController {

    private final UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user));
    }


    // Optional: add signin/login handler if authentication is handled here
    /*
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginRequest) {
        // Implement login logic (e.g., JWT auth)
        return ResponseEntity.ok(authService.login(loginRequest));
    }
    */
}
