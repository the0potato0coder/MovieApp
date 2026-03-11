package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.MovieResponseDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public MovieServiceImpl(MovieRepository movieRepository, TicketRepository ticketRepository) {
        this.movieRepository = movieRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public List<MovieResponseDTO> getAllMoviesWithAvailability() {
        List<Movie> movies = movieRepository.findAll();
        return movies.stream().map(this::toMovieResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<Movie> searchMoviesByName(String movieName) {
        return movieRepository.findByIdMovieNameContainingIgnoreCase(movieName);
    }

    @Override
    public List<MovieResponseDTO> searchMoviesByNameWithAvailability(String movieName) {
        List<Movie> movies = movieRepository.findByIdMovieNameContainingIgnoreCase(movieName);
        return movies.stream().map(this::toMovieResponseDTO).collect(Collectors.toList());
    }

    private MovieResponseDTO toMovieResponseDTO(Movie movie) {
        Integer booked = ticketRepository.sumTicketsBookedForMovieAndTheatre(
                movie.getId().getMovieName(), movie.getId().getTheatreName());
        if (booked == null) booked = 0;
        return MovieResponseDTO.fromMovie(movie, booked);
    }

    @Override
    public Movie updateMovieTickets(String movieName, String theatreName, int newTotalTickets) throws Exception {
        MovieTheatreKey key = new MovieTheatreKey(movieName, theatreName);
        java.util.Optional<Movie> movieOpt = movieRepository.findById(key);

        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            movie.setTotalTicketsAllotted(newTotalTickets);

            Integer alreadyBooked = ticketRepository.sumTicketsBookedForMovieAndTheatre(movieName, theatreName);
            if (alreadyBooked == null) alreadyBooked = 0;

            int availableTickets = newTotalTickets - alreadyBooked;

            if (availableTickets == 0) {
                movie.setTicketStatus("SOLD OUT");
            } else if (availableTickets <= (newTotalTickets / 2)) {
                movie.setTicketStatus("BOOK ASAP");
            } else {
                movie.setTicketStatus("AVAILABLE");
            }

            return movieRepository.save(movie);
        } else {
            throw new Exception("Movie screening not found.");
        }
    }

    @Override
    public void deleteMovie(String movieName, String theatreName) throws Exception {
        MovieTheatreKey key = new MovieTheatreKey(movieName, theatreName);

        if (movieRepository.existsById(key)) {
            movieRepository.deleteById(key);
        } else {
            throw new Exception("Movie not found for deletion.");
        }
    }

    @Override
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateTicketStatusDirect(String movieName, String theatreName, String status) throws Exception {
        MovieTheatreKey key = new MovieTheatreKey(theatreName, movieName);
        java.util.Optional<Movie> movieOpt = movieRepository.findById(key);
        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            movie.setTicketStatus(status);
            return movieRepository.save(movie);
        } else {
            throw new Exception("Movie screening not found.");
        }
    }
}
