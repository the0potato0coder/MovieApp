package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.UserRegistrationDTO;
import com.moviebookingapp.movie_service.model.User;

public interface UserService {
    User registerUser(UserRegistrationDTO registrationDto) throws Exception;
    String loginUser(String identifier, String password) throws Exception;
    String forgotPassword(String username) throws Exception;
}
