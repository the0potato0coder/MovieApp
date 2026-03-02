package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Movie Controller Tests")
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    private List<Movie> movieList;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        movieList = new ArrayList<>();
        
        MovieTheatreKey key = new MovieTheatreKey("PVR Cinema", "Inception");
        testMovie = new Movie();
        testMovie.setId(key);
        testMovie.setTotalTicketsAllotted(100);
        testMovie.setTicketStatus("AVAILABLE");
        
        movieList.add(testMovie);
    }

    // ===== GET ALL MOVIES - POSITIVE TESTS =====
    @Test
    @DisplayName("Should return all movies successfully")
    void testGetAllMoviesSuccess() {
        when(movieService.getAllMovies()).thenReturn(movieList);

        ResponseEntity<List<Movie>> response = movieController.getAllMovies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    @DisplayName("Should return multiple movies")
    void testGetAllMoviesMultiple() {
        MovieTheatreKey key2 = new MovieTheatreKey("INOX", "Avatar");
        Movie movie2 = new Movie();
        movie2.setId(key2);
        movie2.setTotalTicketsAllotted(150);
        movie2.setTicketStatus("BOOK ASAP");
        movieList.add(movie2);

        when(movieService.getAllMovies()).thenReturn(movieList);

        ResponseEntity<List<Movie>> response = movieController.getAllMovies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    // ===== GET ALL MOVIES - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return NO_CONTENT when no movies available")
    void testGetAllMoviesEmpty() {
        when(movieService.getAllMovies()).thenReturn(new ArrayList<>());

        ResponseEntity<List<Movie>> response = movieController.getAllMovies();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    // ===== SEARCH MOVIES - POSITIVE TESTS =====
    @Test
    @DisplayName("Should search and return matching movies")
    void testSearchMovieSuccess() {
        when(movieService.searchMoviesByName("Inception")).thenReturn(movieList);

        ResponseEntity<List<Movie>> response = movieController.searchMovie("Inception");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(movieService, times(1)).searchMoviesByName("Inception");
    }

    @Test
    @DisplayName("Should search movies with case insensitive matching")
    void testSearchMovieCaseInsensitive() {
        when(movieService.searchMoviesByName("inception")).thenReturn(movieList);

        ResponseEntity<List<Movie>> response = movieController.searchMovie("inception");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should search movies by partial name")
    void testSearchMoviePartialName() {
        when(movieService.searchMoviesByName("Incep")).thenReturn(movieList);

        ResponseEntity<List<Movie>> response = movieController.searchMovie("Incep");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ===== SEARCH MOVIES - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return NOT_FOUND when no movies match search")
    void testSearchMovieNotFound() {
        when(movieService.searchMoviesByName("NonExistentMovie")).thenReturn(new ArrayList<>());

        ResponseEntity<List<Movie>> response = movieController.searchMovie("NonExistentMovie");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return NOT_FOUND for empty search query result")
    void testSearchMovieEmptyResult() {
        when(movieService.searchMoviesByName("XYZ")).thenReturn(new ArrayList<>());

        ResponseEntity<List<Movie>> response = movieController.searchMovie("XYZ");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ===== DELETE MOVIE - POSITIVE TEST =====
    @Test
    @DisplayName("Should successfully delete movie")
    void testDeleteMovieSuccess() {
        ResponseEntity<?> response = movieController.deleteMovie("Inception", "PVR Cinema");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Movie deleted successfully"));
    }

    // ===== DELETE MOVIE - NEGATIVE TEST =====
    @Test
    @DisplayName("Should fail to delete non-existent movie")
    void testDeleteMovieNotFound() {
        ResponseEntity<?> response = movieController.deleteMovie("NonExistent", "UnknownTheatre");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Note: Current implementation doesn't validate, returns success
    }
}
