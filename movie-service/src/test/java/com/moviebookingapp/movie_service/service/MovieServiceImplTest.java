package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.MovieResponseDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    MovieRepository movieRepository;
    @Mock
    TicketRepository ticketRepository;

    @InjectMocks
    MovieServiceImpl movieService;

    @Test
    void getAllMovies_returnsMovies_whenPresent() {
        Movie movie = new Movie();
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        List<Movie> result = movieService.getAllMovies();
        assertEquals(1, result.size());
        assertEquals(movie, result.get(0));
    }

    @Test
    void getAllMovies_returnsEmpty_whenNonePresent() {
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());
        List<Movie> result = movieService.getAllMovies();
        assertTrue(result.isEmpty());
    }

    @Test
    void searchMoviesByName_returnsMovies_whenFound() {
        Movie movie = new Movie();
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("test")).thenReturn(List.of(movie));
        List<Movie> result = movieService.searchMoviesByName("test");
        assertEquals(1, result.size());
        assertEquals(movie, result.get(0));
    }

    @Test
    void searchMoviesByName_returnsEmpty_whenNotFound() {
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("test")).thenReturn(Collections.emptyList());
        List<Movie> result = movieService.searchMoviesByName("test");
        assertTrue(result.isEmpty());
    }
}
