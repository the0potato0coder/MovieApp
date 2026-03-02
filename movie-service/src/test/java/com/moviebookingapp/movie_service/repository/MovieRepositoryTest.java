package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Movie Repository Tests")
class MovieRepositoryTest {

    @Mock
    private MovieRepository movieRepository;

    private Movie testMovie;
    private MovieTheatreKey movieKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        movieKey = new MovieTheatreKey("PVR Cinema", "Inception");
        testMovie = new Movie();
        testMovie.setId(movieKey);
        testMovie.setTotalTicketsAllotted(100);
        testMovie.setTicketStatus("AVAILABLE");
    }

    // ===== FIND BY ID - POSITIVE TESTS =====
    @Test
    @DisplayName("Should find movie by movieKey")
    void testFindByIdSuccess() {
        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));

        Optional<Movie> result = movieRepository.findById(movieKey);

        assertTrue(result.isPresent());
        assertEquals("Inception", result.get().getId().getMovieName());
        assertEquals("PVR Cinema", result.get().getId().getTheatreName());
        verify(movieRepository, times(1)).findById(movieKey);
    }

    // ===== FIND BY ID - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty when movie not found")
    void testFindByIdNotFound() {
        MovieTheatreKey nonExistentKey = new MovieTheatreKey("UnknownTheatre", "NonExistent");
        when(movieRepository.findById(nonExistentKey)).thenReturn(Optional.empty());

        Optional<Movie> result = movieRepository.findById(nonExistentKey);

        assertTrue(result.isEmpty());
    }

    // ===== SEARCH BY NAME - POSITIVE TESTS =====
    @Test
    @DisplayName("Should find movies by exact name")
    void testFindByMovieNameExact() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("Inception")).thenReturn(movies);

        List<Movie> result = movieRepository.findByIdMovieNameContainingIgnoreCase("Inception");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Inception", result.get(0).getId().getMovieName());
    }

    @Test
    @DisplayName("Should find movies by partial name")
    void testFindByMovieNamePartial() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("Incep")).thenReturn(movies);

        List<Movie> result = movieRepository.findByIdMovieNameContainingIgnoreCase("Incep");

        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getId().getMovieName().contains("Incep"));
    }

    @Test
    @DisplayName("Should find movies case insensitively")
    void testFindByMovieNameCaseInsensitive() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("inception")).thenReturn(movies);

        List<Movie> result = movieRepository.findByIdMovieNameContainingIgnoreCase("inception");

        assertFalse(result.isEmpty());
        assertEquals("Inception", result.get(0).getId().getMovieName());
    }

    @Test
    @DisplayName("Should find movies with uppercase search")
    void testFindByMovieNameUppercase() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("INCEPTION")).thenReturn(movies);

        List<Movie> result = movieRepository.findByIdMovieNameContainingIgnoreCase("INCEPTION");

        assertFalse(result.isEmpty());
    }

    // ===== SEARCH BY NAME - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when movie name not found")
    void testFindByMovieNameNotFound() {
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("Avatar")).thenReturn(Arrays.asList());

        List<Movie> result = movieRepository.findByIdMovieNameContainingIgnoreCase("Avatar");

        assertTrue(result.isEmpty());
    }

    // ===== SAVE MOVIE - POSITIVE TEST =====
    @Test
    @DisplayName("Should save a new movie")
    void testSaveMovieSuccess() {
        when(movieRepository.save(testMovie)).thenReturn(testMovie);

        Movie result = movieRepository.save(testMovie);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Inception", result.getId().getMovieName());
        verify(movieRepository, times(1)).save(testMovie);
    }

    // ===== DELETE MOVIE - POSITIVE TEST =====
    @Test
    @DisplayName("Should delete a movie by id")
    void testDeleteMovieSuccess() {
        when(movieRepository.existsById(movieKey)).thenReturn(true);

        movieRepository.deleteById(movieKey);

        verify(movieRepository, times(1)).deleteById(movieKey);
    }

    // ===== EXISTS BY ID - POSITIVE TESTS =====
    @Test
    @DisplayName("Should return true when movie exists")
    void testExistsByIdTrue() {
        when(movieRepository.existsById(movieKey)).thenReturn(true);

        boolean exists = movieRepository.existsById(movieKey);

        assertTrue(exists);
        verify(movieRepository, times(1)).existsById(movieKey);
    }

    // ===== EXISTS BY ID - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return false when movie doesn't exist")
    void testExistsByIdFalse() {
        MovieTheatreKey nonExistentKey = new MovieTheatreKey("UnknownTheatre", "NonExistent");
        when(movieRepository.existsById(nonExistentKey)).thenReturn(false);

        boolean exists = movieRepository.existsById(nonExistentKey);

        assertFalse(exists);
    }
}
