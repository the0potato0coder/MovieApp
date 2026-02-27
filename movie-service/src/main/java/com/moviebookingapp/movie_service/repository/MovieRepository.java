package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<com.moviebookingapp.movie_service.model.Movie, MovieTheatreKey> {

    // US_02: Spring Data JPA will automatically generate the SQL to search by movie name, ignoring case!
    List<Movie> findByIdMovieNameContainingIgnoreCase(String movieName);

}