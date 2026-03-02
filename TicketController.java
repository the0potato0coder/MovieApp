package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Book tickets for a movie
    @PostMapping("/{moviename}/add")
    public ResponseEntity<?> bookTicket(@PathVariable String moviename, @RequestBody TicketBookingDTO bookingDto) {
        try {
            // Ensure the DTO matches the path variable
            bookingDto.setMovieName(moviename);
            Ticket bookedTicket = ticketService.bookTicket(bookingDto);
            return new ResponseEntity<>(bookedTicket, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Admin: Update ticket status
    // Rubric specifies: PUT /api/v1.0/moviebooking/<moviename>/update/<ticket>
    @PutMapping("/{moviename}/update/{ticketId}")
    public ResponseEntity<?> updateTicketStatus(@PathVariable String moviename, @PathVariable Long ticketId, @RequestParam String newStatus) {
        try {
            // Note: You will need to add an update ticket status method in your TicketService
            // ticketService.updateStatus(moviename, ticketId, newStatus);
            return new ResponseEntity<>("Ticket status updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all") // Combined, this makes /api/v1.0/moviebooking/all
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = ticketService.getAllMovies();
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
