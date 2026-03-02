package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Movie Service Tests")
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private MovieServiceImpl movieService;

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

    // ===== GET ALL MOVIES - POSITIVE TESTS =====
    @Test
    @DisplayName("Should return all movies")
    void testGetAllMoviesSuccess() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return multiple movies")
    void testGetAllMoviesMultiple() {
        MovieTheatreKey key2 = new MovieTheatreKey("INOX", "Avatar");
        Movie movie2 = new Movie();
        movie2.setId(key2);
        movie2.setTotalTicketsAllotted(150);
        movie2.setTicketStatus("BOOK ASAP");

        List<Movie> movies = Arrays.asList(testMovie, movie2);
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = movieService.getAllMovies();

        assertEquals(2, result.size());
    }

    // ===== GET ALL MOVIES - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when no movies exist")
    void testGetAllMoviesEmpty() {
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());

        List<Movie> result = movieService.getAllMovies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ===== SEARCH MOVIES - POSITIVE TESTS =====
    @Test
    @DisplayName("Should search movies by name")
    void testSearchMoviesByNameSuccess() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("Inception")).thenReturn(movies);

        List<Movie> result = movieService.searchMoviesByName("Inception");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movieRepository, times(1)).findByIdMovieNameContainingIgnoreCase("Inception");
    }

    @Test
    @DisplayName("Should search movies by partial name")
    void testSearchMoviesByPartialName() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("Incep")).thenReturn(movies);

        List<Movie> result = movieService.searchMoviesByName("Incep");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should search movies case insensitively")
    void testSearchMoviesCaseInsensitive() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("inception")).thenReturn(movies);

        List<Movie> result = movieService.searchMoviesByName("inception");

        assertEquals(1, result.size());
    }

    // ===== SEARCH MOVIES - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should return empty list when movie not found")
    void testSearchMoviesNotFound() {
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("NonExistent"))
            .thenReturn(new ArrayList<>());

        List<Movie> result = movieService.searchMoviesByName("NonExistent");

        assertTrue(result.isEmpty());
    }

    // ===== UPDATE MOVIE TICKETS - POSITIVE TESTS =====
    @Test
    @DisplayName("Should update movie tickets and status to AVAILABLE")
    void testUpdateMovieTicketsAvailable() throws Exception {
        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.findById(expectedKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(30);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        Movie result = movieService.updateMovieTickets("Inception", "PVR Cinema", 100);

        assertNotNull(result);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should update movie tickets and status to BOOK ASAP")
    void testUpdateMovieTicketsBookAsap() throws Exception {
        testMovie.setTicketStatus("BOOK ASAP");
        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.findById(expectedKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(51);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        Movie result = movieService.updateMovieTickets("Inception", "PVR Cinema", 100);

        assertNotNull(result);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    @DisplayName("Should update movie tickets and status to SOLD OUT")
    void testUpdateMovieTicketsSoldOut() throws Exception {
        testMovie.setTicketStatus("SOLD OUT");
        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.findById(expectedKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(100);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        Movie result = movieService.updateMovieTickets("Inception", "PVR Cinema", 100);

        assertNotNull(result);
        assertEquals("SOLD OUT", result.getTicketStatus());
    }

    // ===== UPDATE MOVIE TICKETS - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail to update tickets for non-existent movie")
    void testUpdateMovieTicketsNotFound() {
        when(movieRepository.findById(movieKey)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> movieService.updateMovieTickets("Inception", "PVR Cinema", 100));
    }

    // ===== ADD MOVIE - POSITIVE TEST =====
    @Test
    @DisplayName("Should successfully add a new movie")
    void testAddMovieSuccess() {
        when(movieRepository.save(testMovie)).thenReturn(testMovie);

        Movie result = movieService.addMovie(testMovie);

        assertNotNull(result);
        assertEquals("Inception", result.getId().getMovieName());
        verify(movieRepository, times(1)).save(testMovie);
    }

    // ===== DELETE MOVIE - POSITIVE TESTS =====
    @Test
    @DisplayName("Should successfully delete a movie")
    void testDeleteMovieSuccess() throws Exception {
        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.existsById(expectedKey)).thenReturn(true);

        movieService.deleteMovie("Inception", "PVR Cinema");

        verify(movieRepository, times(1)).deleteById(expectedKey);
    }

    // ===== DELETE MOVIE - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail to delete non-existent movie")
    void testDeleteMovieNotFound() {
        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.existsById(expectedKey)).thenReturn(false);

        assertThrows(Exception.class, () -> movieService.deleteMovie("Inception", "PVR Cinema"));
    }

    // ===== UPDATE TICKET STATUS - POSITIVE TEST =====
    @Test
    @DisplayName("Should update ticket status")
    void testUpdateTicketStatusSuccess() throws Exception {
        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        Movie result = movieService.updateTicketStatusDirect("Inception", "PVR Cinema", "SOLD OUT");

        assertNotNull(result);
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    // ===== UPDATE TICKET STATUS - NEGATIVE TEST =====
    @Test
    @DisplayName("Should fail to update status for non-existent movie")
    void testUpdateTicketStatusNotFound() {
        when(movieRepository.findById(movieKey)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> movieService.updateTicketStatusDirect("Inception", "PVR Cinema", "SOLD OUT"));
    }
}
