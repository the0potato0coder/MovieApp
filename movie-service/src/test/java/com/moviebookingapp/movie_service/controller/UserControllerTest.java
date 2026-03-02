package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.Role;
import com.moviebookingapp.movie_service.model.User;
import com.moviebookingapp.movie_service.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Controller Tests")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserRegistrationDTO validRegistrationDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        validRegistrationDTO = new UserRegistrationDTO();
        validRegistrationDTO.setFirstName("John");
        validRegistrationDTO.setLastName("Doe");
        validRegistrationDTO.setEmail("john@example.com");
        validRegistrationDTO.setUsername("johndoe");
        validRegistrationDTO.setPassword("Password123");
        validRegistrationDTO.setConfirmPassword("Password123");
        validRegistrationDTO.setContactNumber("9876543210");
        validRegistrationDTO.setRole("USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john@example.com");
        testUser.setUsername("johndoe");
        testUser.setPassword("Password123");
        testUser.setContactNumber("9876543210");
        testUser.setRole(Role.USER);
    }

    // ===== REGISTER USER - POSITIVE TESTS =====
    @Test
    @DisplayName("Should successfully register a new user")
    void testRegisterUserSuccess() throws Exception {
        when(userService.registerUser(validRegistrationDTO)).thenReturn(testUser);

        ResponseEntity<?> response = userController.registerUser(validRegistrationDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).registerUser(validRegistrationDTO);
    }

    @Test
    @DisplayName("Should register user with default CUSTOMER role")
    void testRegisterUserWithDefaultRole() throws Exception {
        validRegistrationDTO.setRole(null);
        when(userService.registerUser(validRegistrationDTO)).thenReturn(testUser);

        ResponseEntity<?> response = userController.registerUser(validRegistrationDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService, times(1)).registerUser(validRegistrationDTO);
    }

    // ===== REGISTER USER - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail to register user with missing first name")
    void testRegisterUserMissingFirstName() throws Exception {
        validRegistrationDTO.setFirstName(null);
        when(userService.registerUser(validRegistrationDTO))
            .thenThrow(new Exception("First Name is required!"));

        ResponseEntity<?> response = userController.registerUser(validRegistrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("First Name is required!"));
    }

    @Test
    @DisplayName("Should fail to register user with mismatched passwords")
    void testRegisterUserPasswordMismatch() throws Exception {
        validRegistrationDTO.setConfirmPassword("DifferentPassword");
        when(userService.registerUser(validRegistrationDTO))
            .thenThrow(new Exception("Passwords do not match!"));

        ResponseEntity<?> response = userController.registerUser(validRegistrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Passwords do not match!"));
    }

    @Test
    @DisplayName("Should fail to register user with duplicate username")
    void testRegisterUserDuplicateUsername() throws Exception {
        when(userService.registerUser(validRegistrationDTO))
            .thenThrow(new Exception("Login ID is already taken!"));

        ResponseEntity<?> response = userController.registerUser(validRegistrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Login ID is already taken!"));
    }

    @Test
    @DisplayName("Should fail to register user with duplicate email")
    void testRegisterUserDuplicateEmail() throws Exception {
        when(userService.registerUser(validRegistrationDTO))
            .thenThrow(new Exception("Email is already registered!"));

        ResponseEntity<?> response = userController.registerUser(validRegistrationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Email is already registered!"));
    }

    // ===== LOGIN USER - POSITIVE TESTS =====
    @Test
    @DisplayName("Should successfully login user and return JWT token")
    void testLoginUserSuccess() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(userService.loginUser("johndoe", "Password123")).thenReturn(jwtToken);

        ResponseEntity<?> response = userController.loginUser("johndoe", "Password123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("token"));
        verify(userService, times(1)).loginUser("johndoe", "Password123");
    }

    // ===== LOGIN USER - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail login with invalid username")
    void testLoginUserInvalidUsername() throws Exception {
        when(userService.loginUser("invaliduser", "Password123"))
            .thenThrow(new Exception("User not found"));

        ResponseEntity<?> response = userController.loginUser("invaliduser", "Password123");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User not found"));
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    void testLoginUserIncorrectPassword() throws Exception {
        when(userService.loginUser("johndoe", "WrongPassword"))
            .thenThrow(new Exception("Invalid credentials"));

        ResponseEntity<?> response = userController.loginUser("johndoe", "WrongPassword");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Invalid credentials"));
    }

    // ===== FORGOT PASSWORD - POSITIVE TEST =====
    @Test
    @DisplayName("Should return success message for forgot password")
    void testForgotPasswordSuccess() {
        ResponseEntity<?> response = userController.forgotPassword("johndoe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Password reset instructions"));
    }
}
