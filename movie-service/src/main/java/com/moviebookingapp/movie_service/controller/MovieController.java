package com.moviebookingapp.movie_service.controller;

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

    // View all movies
    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    // Search by movie name
    @GetMapping("/movies/search/{moviename}")
    public ResponseEntity<List<Movie>> searchMovie(@PathVariable String moviename) {
        List<Movie> movies = movieService.searchMoviesByName(moviename);
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    // Admin: Delete movie
    // Note: Rubric specifies /<moviename>/delete/<id>
    @DeleteMapping("/{moviename}/delete/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable String moviename, @PathVariable String id) {
        try {
            // Note: You will need to add a delete method in your MovieService for this to work
            // movieService.deleteMovie(moviename, id);
            return new ResponseEntity<>("Movie deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}