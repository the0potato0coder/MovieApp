package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final MovieRepository movieRepository;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, MovieRepository movieRepository) {
        this.ticketRepository = ticketRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public Ticket bookTicket(TicketBookingDTO dto) throws Exception {
        // 1. Verify the seat count matches the number of tickets requested
        if (dto.getNumberOfTickets() != dto.getSeatNumbers().size()) {
            throw new Exception("Number of tickets does not match the number of seats selected.");
        }

        // 2. Find the exact movie/theatre combination using our Composite Key
        MovieTheatreKey key = new MovieTheatreKey(dto.getMovieName(), dto.getTheatreName());
        Optional<Movie> movieOptional = movieRepository.findById(key);

        if (movieOptional.isEmpty()) {
            throw new Exception("Movie not found in the specified theatre.");
        }
        Movie movie = movieOptional.get();

        // 3. Inventory Check: Calculate available tickets
        // We use the custom query we wrote earlier to sum up all booked tickets
        Integer alreadyBooked = ticketRepository.sumTicketsBookedForMovieAndTheatre(dto.getMovieName(), dto.getTheatreName());
        if (alreadyBooked == null) {
            alreadyBooked = 0; // Handle the case where 0 tickets have been booked so far
        }

        int availableTickets = movie.getTotalTicketsAllotted() - alreadyBooked;

        // 4. Validate if there are enough seats left
        if (dto.getNumberOfTickets() > availableTickets) {
            throw new Exception("Only " + availableTickets + " tickets are available for this screening.");
        }

        // 5. Create and save the Ticket
        Ticket newTicket = new Ticket(movie, dto.getNumberOfTickets(), dto.getSeatNumbers());
        return ticketRepository.save(newTicket);
    }

    // Add this inside TicketServiceImpl.java
    @Override
    public java.util.List<Ticket> getBookedTickets(String movieName) {
        return ticketRepository.findByMovieName(movieName);
    }

    @Override
    public java.util.List<Movie> getAllMovies() {
        return movieRepository.findAll();}

}