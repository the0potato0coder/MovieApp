package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.Role;
import com.moviebookingapp.movie_service.model.User;
import com.moviebookingapp.movie_service.repository.UserRepository;
import com.moviebookingapp.movie_service.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDTO validDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validDTO = new UserRegistrationDTO();
        validDTO.setFirstName("John");
        validDTO.setLastName("Doe");
        validDTO.setEmail("john@example.com");
        validDTO.setUsername("johndoe");
        validDTO.setPassword("Password123");
        validDTO.setConfirmPassword("Password123");
        validDTO.setContactNumber("9876543210");
        validDTO.setRole("USER");

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
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByContactNumber("9876543210")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(validDTO);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user with ADMIN role")
    void testRegisterUserAsAdmin() throws Exception {
        validDTO.setRole("ADMIN");
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByContactNumber("9876543210")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(validDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ===== REGISTER USER - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail to register user with missing first name")
    void testRegisterUserMissingFirstName() {
        validDTO.setFirstName(null);

        assertThrows(Exception.class, () -> userService.registerUser(validDTO));
    }

    @Test
    @DisplayName("Should fail to register user with empty first name")
    void testRegisterUserEmptyFirstName() {
        validDTO.setFirstName("   ");

        assertThrows(Exception.class, () -> userService.registerUser(validDTO));
    }

    @Test
    @DisplayName("Should fail to register user with mismatched passwords")
    void testRegisterUserPasswordMismatch() {
        validDTO.setConfirmPassword("DifferentPassword");

        Exception exception = assertThrows(Exception.class, () -> userService.registerUser(validDTO));
        assertTrue(exception.getMessage().contains("do not match"));
    }

    @Test
    @DisplayName("Should fail to register user with duplicate username")
    void testRegisterUserDuplicateUsername() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> userService.registerUser(validDTO));
        assertTrue(exception.getMessage().contains("already taken"));
    }

    @Test
    @DisplayName("Should fail to register user with duplicate email")
    void testRegisterUserDuplicateEmail() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> userService.registerUser(validDTO));
        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("Should fail to register user with duplicate contact number")
    void testRegisterUserDuplicateContact() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.existsByContactNumber("9876543210")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> userService.registerUser(validDTO));
        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("Should fail to register user with missing email")
    void testRegisterUserMissingEmail() {
        validDTO.setEmail(null);

        assertThrows(Exception.class, () -> userService.registerUser(validDTO));
    }

    @Test
    @DisplayName("Should fail to register user with missing username")
    void testRegisterUserMissingUsername() {
        validDTO.setUsername(null);

        assertThrows(Exception.class, () -> userService.registerUser(validDTO));
    }

    @Test
    @DisplayName("Should fail to register user with missing password")
    void testRegisterUserMissingPassword() {
        validDTO.setPassword(null);

        assertThrows(Exception.class, () -> userService.registerUser(validDTO));
    }

    @Test
    @DisplayName("Should fail to register user with missing contact number")
    void testRegisterUserMissingContact() {
        validDTO.setContactNumber(null);

        assertThrows(Exception.class, () -> userService.registerUser(validDTO));
    }

    // ===== LOGIN USER - POSITIVE TESTS =====
    @Test
    @DisplayName("Should successfully login user by username")
    void testLoginUserByUsernameSuccess() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(userRepository.findByLoginIdentifier("johndoe")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken("johndoe", "USER")).thenReturn(jwtToken);

        Object token = userService.loginUser("johndoe", "Password123");

        assertNotNull(token);
        verify(userRepository, times(1)).findByLoginIdentifier("johndoe");
    }

    @Test
    @DisplayName("Should successfully login user by email")
    void testLoginUserByEmailSuccess() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(userRepository.findByLoginIdentifier("john@example.com")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken("johndoe", "USER")).thenReturn(jwtToken);

        Object token = userService.loginUser("john@example.com", "Password123");

        assertNotNull(token);
    }

    @Test
    @DisplayName("Should successfully login user by contact number")
    void testLoginUserByContactSuccess() throws Exception {
        String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        when(userRepository.findByLoginIdentifier("9876543210")).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken("johndoe", "USER")).thenReturn(jwtToken);

        Object token = userService.loginUser("9876543210", "Password123");

        assertNotNull(token);
    }

    // ===== LOGIN USER - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail login with non-existent user")
    void testLoginUserNotFound() {
        when(userRepository.findByLoginIdentifier("nonexistent")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> userService.loginUser("nonexistent", "Password123"));
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    void testLoginUserIncorrectPassword() {
        when(userRepository.findByLoginIdentifier("johndoe")).thenReturn(Optional.of(testUser));

        assertThrows(Exception.class, () -> userService.loginUser("johndoe", "WrongPassword"));
    }
}
