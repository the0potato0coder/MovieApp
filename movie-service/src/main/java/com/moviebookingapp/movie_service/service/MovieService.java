package com.moviebookingapp.movie_service.service;


import com.moviebookingapp.movie_service.dto.MovieResponseDTO;
import com.moviebookingapp.movie_service.model.Movie;

import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();
    List<MovieResponseDTO> getAllMoviesWithAvailability();
    List<Movie> searchMoviesByName(String movieName);
    List<MovieResponseDTO> searchMoviesByNameWithAvailability(String movieName);
    void deleteMovie(String movieName, String theatreName) throws Exception;

    Movie updateMovieTickets(String movieName, String theatreName, int newTotalTickets) throws Exception;

    Movie addMovie(Movie movie);

    Movie updateTicketStatusDirect(String movieName, String theatreName, String status) throws Exception;
}
