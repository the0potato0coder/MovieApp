package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.User;
import com.moviebookingapp.movie_service.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    @Test
    void registerUser_returnsCreated_whenSuccess() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        User user = new User();
        doReturn(user).when(userService).registerUser(dto);
        ResponseEntity<?> response = userController.registerUser(dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
    }

    @Test
    void registerUser_returnsBadRequest_whenException() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        doThrow(new Exception("fail")).when(userService).registerUser(dto);
        ResponseEntity<?> response = userController.registerUser(dto);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("fail", response.getBody());
    }
}
