package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.User;
import com.moviebookingapp.movie_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register as new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO registrationDto) {
        try {
            User registeredUser = userService.registerUser(registrationDto);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<?> loginUser(@RequestParam String username, @RequestParam String password) {
        try {
            // This now returns the JWT string
            String token = String.valueOf(userService.loginUser(username, password));

            // It is standard practice to return the token in a JSON object
            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("token", token);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // Forgot password (request reset token)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String identifier) {
        try {
            String message = userService.forgotPassword(identifier);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Reset password (with token)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            String message = userService.resetPassword(token, newPassword);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}