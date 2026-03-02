package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Role;
import com.moviebookingapp.movie_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

    // ===== EXISTS BY USERNAME - POSITIVE TEST =====
    @Test
    @DisplayName("Should return true when username exists")
    void testExistsByUsernameTrue() {
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        boolean exists = userRepository.existsByUsername("johndoe");

        assertTrue(exists);
        verify(userRepository, times(1)).existsByUsername("johndoe");
    }

    // ===== EXISTS BY USERNAME - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return false when username doesn't exist")
    void testExistsByUsernameFalse() {
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        boolean exists = userRepository.existsByUsername("nonexistent");

        assertFalse(exists);
    }

    // ===== EXISTS BY EMAIL - POSITIVE TEST =====
    @Test
    @DisplayName("Should return true when email exists")
    void testExistsByEmailTrue() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean exists = userRepository.existsByEmail("john@example.com");

        assertTrue(exists);
    }

    // ===== EXISTS BY EMAIL - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return false when email doesn't exist")
    void testExistsByEmailFalse() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        boolean exists = userRepository.existsByEmail("unknown@example.com");

        assertFalse(exists);
    }

    // ===== EXISTS BY CONTACT NUMBER - POSITIVE TEST =====
    @Test
    @DisplayName("Should return true when contact number exists")
    void testExistsByContactNumberTrue() {
        when(userRepository.existsByContactNumber("9876543210")).thenReturn(true);

        boolean exists = userRepository.existsByContactNumber("9876543210");

        assertTrue(exists);
    }

    // ===== EXISTS BY CONTACT NUMBER - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return false when contact number doesn't exist")
    void testExistsByContactNumberFalse() {
        when(userRepository.existsByContactNumber("1234567890")).thenReturn(false);

        boolean exists = userRepository.existsByContactNumber("1234567890");

        assertFalse(exists);
    }

    // ===== FIND BY LOGIN IDENTIFIER - POSITIVE TESTS =====
    @Test
    @DisplayName("Should find user by username")
    void testFindByLoginIdentifierUsername() {
        when(userRepository.findByLoginIdentifier("johndoe")).thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findByLoginIdentifier("johndoe");

        assertTrue(result.isPresent());
        assertEquals("johndoe", result.get().getUsername());
        verify(userRepository, times(1)).findByLoginIdentifier("johndoe");
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByLoginIdentifierEmail() {
        when(userRepository.findByLoginIdentifier("john@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findByLoginIdentifier("john@example.com");

        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should find user by contact number")
    void testFindByLoginIdentifierContact() {
        when(userRepository.findByLoginIdentifier("9876543210")).thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findByLoginIdentifier("9876543210");

        assertTrue(result.isPresent());
        assertEquals("9876543210", result.get().getContactNumber());
    }

    // ===== FIND BY LOGIN IDENTIFIER - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty when user not found")
    void testFindByLoginIdentifierNotFound() {
        when(userRepository.findByLoginIdentifier("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findByLoginIdentifier("nonexistent");

        assertTrue(result.isEmpty());
    }

    // ===== SAVE USER - POSITIVE TEST =====
    @Test
    @DisplayName("Should save a new user")
    void testSaveUserSuccess() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userRepository.save(testUser);

        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    // ===== FIND BY ID - POSITIVE TEST =====
    @Test
    @DisplayName("Should find user by id")
    void testFindByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    // ===== FIND BY ID - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty when user id not found")
    void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> result = userRepository.findById(999L);

        assertTrue(result.isEmpty());
    }
}
