package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    UserRepository userRepository;

    @Test
    void saveAndFindUserByLoginIdentifier_withMockito() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("pass");
        user.setEmail("test@example.com");
        when(userRepository.findByLoginIdentifier("testuser")).thenReturn(Optional.of(user));
        Optional<User> found = userRepository.findByLoginIdentifier("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        verify(userRepository, times(1)).findByLoginIdentifier("testuser");
    }

    @Test
    void findByLoginIdentifier_returnsEmpty_withMockito() {
        when(userRepository.findByLoginIdentifier("nouser")).thenReturn(Optional.empty());
        Optional<User> found = userRepository.findByLoginIdentifier("nouser");
        assertFalse(found.isPresent());
        verify(userRepository, times(1)).findByLoginIdentifier("nouser");
    }
}
