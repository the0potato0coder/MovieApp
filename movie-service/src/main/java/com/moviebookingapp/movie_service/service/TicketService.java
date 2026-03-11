package com.moviebookingapp.movie_service.service;


import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.Ticket;

import java.util.List;

public interface TicketService {
    Ticket bookTicket(TicketBookingDTO dto) throws Exception;

    java.util.List<Ticket> getBookedTickets(String movieName);
    java.util.List<Ticket> getTicketsByUsername(String username);

    List<Movie> getAllMovies();
    List<String> getBookedSeats(String movieName, String theatreName);
}