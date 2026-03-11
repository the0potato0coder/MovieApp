package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.MovieResponseDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieResponseDTO>> getAllMovies() {
        List<MovieResponseDTO> movies = movieService.getAllMoviesWithAvailability();
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/movies/search/{moviename}")
    public ResponseEntity<List<MovieResponseDTO>> searchMovie(@PathVariable String moviename) {
        List<MovieResponseDTO> movies = movieService.searchMoviesByNameWithAvailability(moviename);
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @DeleteMapping("/{moviename}/delete/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable String moviename, @PathVariable String id) {
        try {
            return new ResponseEntity<>("Movie deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}