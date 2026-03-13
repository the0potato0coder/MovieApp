package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieRepositoryTest {

    @Mock
    MovieRepository movieRepository;

    @Test
    void findByIdMovieNameContainingIgnoreCase_returnsMovies_withMockito() {
        MovieTheatreKey key = new MovieTheatreKey("theatre", "TestMovie");
        Movie movie = new Movie();
        movie.setId(key);
        movie.setTotalTicketsAllotted(10);
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("test")).thenReturn(List.of(movie));
        List<Movie> found = movieRepository.findByIdMovieNameContainingIgnoreCase("test");
        assertFalse(found.isEmpty());
        assertEquals("TestMovie", found.get(0).getMovieName());
        verify(movieRepository, times(1)).findByIdMovieNameContainingIgnoreCase("test");
    }

    @Test
    void findByIdMovieNameContainingIgnoreCase_returnsEmpty_withMockito() {
        when(movieRepository.findByIdMovieNameContainingIgnoreCase("none")).thenReturn(List.of());
        List<Movie> found = movieRepository.findByIdMovieNameContainingIgnoreCase("none");
        assertTrue(found.isEmpty());
        verify(movieRepository, times(1)).findByIdMovieNameContainingIgnoreCase("none");
    }
}
