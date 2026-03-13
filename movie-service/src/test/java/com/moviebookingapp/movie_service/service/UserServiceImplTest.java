package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.User;
import com.moviebookingapp.movie_service.repository.UserRepository;
import com.moviebookingapp.movie_service.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void registerUser_successful() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setUsername("johndoe");
        dto.setPassword("pass");
        dto.setConfirmPassword("pass");
        dto.setContactNumber("1234567890");
        dto.setRole("USER");
        User user = new User();
        when(userRepository.save(any())).thenReturn(user);
        User result = userService.registerUser(dto);
        assertEquals(user, result);
    }

    @Test
    void registerUser_throwsException_whenMissingFirstName() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setFirstName("");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setUsername("johndoe");
        dto.setPassword("pass");
        dto.setConfirmPassword("pass");
        dto.setContactNumber("1234567890");
        dto.setRole("USER");
        Exception ex = assertThrows(Exception.class, () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("First Name is required"));
    }

    @Test
    void registerUser_throwsException_whenPasswordMismatch() {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setEmail("john@example.com");
        dto.setUsername("johndoe");
        dto.setPassword("pass");
        dto.setConfirmPassword("wrong");
        dto.setContactNumber("1234567890");
        dto.setRole("USER");
        Exception ex = assertThrows(Exception.class, () -> userService.registerUser(dto));
        assertTrue(ex.getMessage().contains("Passwords do not match"));
    }
}
