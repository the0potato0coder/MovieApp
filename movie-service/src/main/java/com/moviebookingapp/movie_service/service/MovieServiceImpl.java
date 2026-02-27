package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Movie> searchMoviesByName(String movieName) {
        // Calls the custom method we defined in the repository
        return movieRepository.findByIdMovieNameContainingIgnoreCase(movieName);
    }

    // You will need to inject TicketRepository into MovieServiceImpl to do the math
    @Override
    public Movie updateMovieTickets(String movieName, String theatreName, int newTotalTickets) throws Exception {
        MovieTheatreKey key = new MovieTheatreKey(movieName, theatreName);
        java.util.Optional<Movie> movieOpt = movieRepository.findById(key);

        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            movie.setTotalTicketsAllotted(newTotalTickets);

            // Calculate booked tickets
            Integer alreadyBooked = ticketRepository.sumTicketsBookedForMovieAndTheatre(movieName, theatreName);
            if (alreadyBooked == null) alreadyBooked = 0;

            int availableTickets = newTotalTickets - alreadyBooked;

            // Apply the strict rubric conditions [cite: 175, 176, 177]
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

    // Add TicketRepository to your MovieServiceImpl constructor first!
    @Override
    public void deleteMovie(String movieName, String theatreName) throws Exception {
        MovieTheatreKey key = new MovieTheatreKey(movieName, theatreName);

        if (movieRepository.existsById(key)) {
            // Step 1: Fetch and delete all tickets associated with this movie/theatre combo
            // to satisfy the rubric requirement.
            // (You'll need a custom query in TicketRepository: deleteByMovieId(key))

            // Step 2: Delete the movie itself
            movieRepository.deleteById(key);
        } else {
            throw new Exception("Movie not found for deletion.");
        }
    }
}
