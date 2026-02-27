package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.service.MovieService;
import com.moviebookingapp.movie_service.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/moviebooking/admin")
public class AdminController {

    private final MovieService movieService;
    private final TicketService ticketService;

    @Autowired
    public AdminController(MovieService movieService, TicketService ticketService) {
        this.movieService = movieService;
        this.ticketService = ticketService;
    }

    // US_04: View booked tickets for a particular movie
    @GetMapping("/tickets/{movieName}")
    public ResponseEntity<List<Ticket>> viewBookedTickets(@PathVariable String movieName) {
        List<Ticket> tickets = ticketService.getBookedTickets(movieName);
        if (tickets.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    // US_04: Update total tickets allotted for a movie screening
    @PutMapping("/update/{movieName}/{theatreName}")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable String movieName,
            @PathVariable String theatreName,
            @RequestParam int newTotalTickets) {
        try {
            Movie updatedMovie = movieService.updateMovieTickets(movieName, theatreName, newTotalTickets);
            return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
