package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        if (dto.getNumberOfTickets() != dto.getSeatNumbers().size()) {
            throw new Exception("Number of tickets does not match the number of seats selected.");
        }

        MovieTheatreKey key = new MovieTheatreKey(dto.getTheatreName(), dto.getMovieName());
        Optional<Movie> movieOptional = movieRepository.findById(key);

        if (movieOptional.isEmpty()) {
            throw new Exception("Movie not found in the specified theatre.");
        }
        Movie movie = movieOptional.get();

        Integer alreadyBooked = ticketRepository.sumTicketsBookedForMovieAndTheatre(dto.getMovieName(), dto.getTheatreName());
        if (alreadyBooked == null) {
            alreadyBooked = 0;
        }

        int availableTickets = movie.getTotalTicketsAllotted() - alreadyBooked;

        if (dto.getNumberOfTickets() > availableTickets) {
            throw new Exception("Only " + availableTickets + " tickets are available for this screening.");
        }

        List<Ticket> existingTickets = ticketRepository.findByMovieNameAndTheatreName(dto.getMovieName(), dto.getTheatreName());
        Set<String> bookedSeats = new HashSet<>();
        for (Ticket t : existingTickets) {
            if (t.getSeatNumbers() != null) bookedSeats.addAll(t.getSeatNumbers());
        }
        for (String seat : dto.getSeatNumbers()) {
            if (bookedSeats.contains(seat)) {
                throw new Exception("Seat " + seat + " is already booked. Please select another seat.");
            }
        }
        List<String> seats = new ArrayList<>(dto.getSeatNumbers());
        seats.sort((a, b) -> a.compareTo(b));
        for (int i = 1; i < seats.size(); i++) {
            String prev = seats.get(i - 1);
            String curr = seats.get(i);
            String prevRow = prev.replaceAll("\\d", "");
            String currRow = curr.replaceAll("\\d", "");
            int prevNum = Integer.parseInt(prev.replaceAll("\\D", ""));
            int currNum = Integer.parseInt(curr.replaceAll("\\D", ""));
            if (!prevRow.equals(currRow) || currNum != prevNum + 1) {
                throw new Exception("Seats must be adjacent in the same row (e.g., A1, A2, A3).");
            }
        }
        Ticket newTicket = new Ticket(movie, dto.getNumberOfTickets(), dto.getSeatNumbers(), dto.getUsername());
        return ticketRepository.save(newTicket);
    }

    @Override
    public java.util.List<Ticket> getBookedTickets(String movieName) {
        return ticketRepository.findByMovieName(movieName);
    }

    @Override
    public java.util.List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public java.util.List<Ticket> getTicketsByUsername(String username) {
        return ticketRepository.findByUsername(username);
    }

    @Override
    public List<String> getBookedSeats(String movieName, String theatreName) {
        List<Ticket> tickets = ticketRepository.findByMovieNameAndTheatreName(movieName, theatreName);
        Set<String> bookedSeats = new HashSet<>();
        for (Ticket t : tickets) {
            if (t.getSeatNumbers() != null) bookedSeats.addAll(t.getSeatNumbers());
        }
        return new ArrayList<>(bookedSeats);
    }
}