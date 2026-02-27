package com.moviebookingapp.movie_service.service;


import com.moviebookingapp.movie_service.model.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();
    List<Movie> searchMoviesByName(String movieName);
    void deleteMovie(String movieName, String theatreName) throws Exception;

    Movie updateMovieTickets(String movieName, String theatreName, int newTotalTickets) throws Exception;
}
