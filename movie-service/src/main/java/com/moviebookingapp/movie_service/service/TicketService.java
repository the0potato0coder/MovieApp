package com.moviebookingapp.movie_service.service;


import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.Ticket;

import java.util.List;

public interface TicketService {
    Ticket bookTicket(TicketBookingDTO dto) throws Exception;

    // Add this to TicketService.java
    java.util.List<Ticket> getBookedTickets(String movieName);

    List<Movie> getAllMovies();
}