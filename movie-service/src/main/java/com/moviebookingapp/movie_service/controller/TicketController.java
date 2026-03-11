package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/{movieName}/add")
    public ResponseEntity<?> bookTicket(@PathVariable String movieName, @RequestBody TicketBookingDTO bookingDTO) {
        try {
            bookingDTO.setMovieName(movieName);
            Ticket bookedTicket = ticketService.bookTicket(bookingDTO);
            return ResponseEntity.ok(bookedTicket);
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyTickets(@RequestParam String username) {
        try {
            return ResponseEntity.ok(ticketService.getTicketsByUsername(username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/seats/booked")
    public ResponseEntity<?> getBookedSeats(@RequestParam String movieName, @RequestParam String theatreName) {
        try {
            return ResponseEntity.ok(ticketService.getBookedSeats(movieName, theatreName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
