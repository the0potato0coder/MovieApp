package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.MovieResponseDTO;
import com.moviebookingapp.movie_service.service.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    MovieService movieService;

    @InjectMocks
    MovieController movieController;

    @Test
    void getAllMovies_returnsNoContent_whenEmpty() {
        when(movieService.getAllMoviesWithAvailability()).thenReturn(Collections.emptyList());
        ResponseEntity<List<MovieResponseDTO>> response = movieController.getAllMovies();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getAllMovies_returnsOk_whenMoviesPresent() {
        MovieResponseDTO dto = new MovieResponseDTO();
        when(movieService.getAllMoviesWithAvailability()).thenReturn(List.of(dto));
        ResponseEntity<List<MovieResponseDTO>> response = movieController.getAllMovies();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void searchMovie_returnsNotFound_whenEmpty() {
        when(movieService.searchMoviesByNameWithAvailability("test")).thenReturn(Collections.emptyList());
        ResponseEntity<List<MovieResponseDTO>> response = movieController.searchMovie("test");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void searchMovie_returnsOk_whenMoviesFound() {
        MovieResponseDTO dto = new MovieResponseDTO();
        when(movieService.searchMoviesByNameWithAvailability("test")).thenReturn(List.of(dto));
        ResponseEntity<List<MovieResponseDTO>> response = movieController.searchMovie("test");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}
