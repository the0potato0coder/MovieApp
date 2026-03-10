package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.Role;
import com.moviebookingapp.movie_service.model.User;
import com.moviebookingapp.movie_service.repository.UserRepository;
import com.moviebookingapp.movie_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User registerUser(UserRegistrationDTO dto) throws Exception {
        // Mandatory field validation
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new Exception("First Name is required!");
        }
        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new Exception("Last Name is required!");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new Exception("Email is required!");
        }
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new Exception("Login ID is required!");
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new Exception("Password is required!");
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().trim().isEmpty()) {
            throw new Exception("Confirm Password is required!");
        }
        if (dto.getContactNumber() == null || dto.getContactNumber().trim().isEmpty()) {
            throw new Exception("Contact Number is required!");
        }

        // 1. Client Requirement: Password and Confirm Password must be the same
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new Exception("Passwords do not match!");
        }

        // 2. Client Requirement: Login Id and Email must be unique (and we added contact number)
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new Exception("Login ID is already taken!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new Exception("Email is already registered!");
        }
        if (userRepository.existsByContactNumber(dto.getContactNumber())) {
            throw new Exception("Contact number is already registered!");
        }

        // 3. Map the DTO to our actual Database Entity
        User newUser = new User();
        newUser.setFirstName(dto.getFirstName());
        newUser.setLastName(dto.getLastName());
        newUser.setEmail(dto.getEmail());
        newUser.setUsername(dto.getUsername());
        newUser.setContactNumber(dto.getContactNumber());
        newUser.setRole(Role.valueOf(dto.getRole() != null ? dto.getRole() : "CUSTOMER"));

        // Note: In a production app, we would hash this password using BCrypt before saving!
        // For this MVP step, we are saving it as plain text to get the logic flowing.
        newUser.setPassword(dto.getPassword());

        // 4. Save to database
        return userRepository.save(newUser);
    }

    @Override
    public String loginUser(String identifier, String password) throws Exception {
        Optional<User> userOptional = userRepository.findByLoginIdentifier(identifier);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // SUCCESS! Generate and return the JWT instead of the User object
                // We pass the loginId and the Role (e.g., "CUSTOMER" or "ADMIN")
                return jwtUtil.generateToken(user.getUsername(), String.valueOf(user.getRole()));
            } else {
                throw new Exception("Invalid password.");
            }
        } else {
            throw new Exception("User not found with identifier: " + identifier);
        }
    }

    @Override
    public String forgotPassword(String identifier) throws Exception {
        Optional<User> userOptional = userRepository.findByLoginIdentifier(identifier);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Generate a random token
            String token = java.util.UUID.randomUUID().toString();
            user.setResetToken(token);
            user.setResetTokenExpiry(java.time.LocalDateTime.now().plusMinutes(30));
            userRepository.save(user);
            // In production, send email. Here, return token for demo/testing.
            return "Password reset token generated. Use this token to reset your password: " + token;
        } else {
            throw new Exception("User not found with identifier: " + identifier);
        }
    }

    @Override
    public String resetPassword(String token, String newPassword) throws Exception {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
                throw new Exception("Reset token has expired. Please request a new one.");
            }
            user.setPassword(newPassword); // In production, hash the password!
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return "Password has been reset successfully.";
        } else {
            throw new Exception("Invalid or expired reset token.");
        }
    }
}