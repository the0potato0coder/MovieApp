package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Ticket Controller Tests")
class TicketControllerTest {

    @Mock
    private TicketService ticketService;

    @InjectMocks
    private TicketController ticketController;

    private TicketBookingDTO validBookingDTO;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        validBookingDTO = new TicketBookingDTO();
        validBookingDTO.setMovieName("Inception");
        validBookingDTO.setTheatreName("PVR Cinema");
        validBookingDTO.setNumberOfTickets(2L);
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "A2"));
        validBookingDTO.setUsername("johndoe");

        MovieTheatreKey key = new MovieTheatreKey("PVR Cinema", "Inception");
        Movie movie = new Movie();
        movie.setId(key);
        movie.setTotalTicketsAllotted(100);
        movie.setTicketStatus("AVAILABLE");

        testTicket = new Ticket();
        testTicket.setTransactionId(1L);
        testTicket.setMovie(movie);
        testTicket.setNumberOfTickets(2L);
        testTicket.setSeatNumbers(Arrays.asList("A1", "A2"));
        testTicket.setUsername("johndoe");
    }

    // ===== BOOK TICKET - POSITIVE TESTS =====
    @Test
    @DisplayName("Should successfully book a ticket")
    void testBookTicketSuccess() throws Exception {
        when(ticketService.bookTicket(validBookingDTO)).thenReturn(testTicket);

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTicket, response.getBody());
        verify(ticketService, times(1)).bookTicket(validBookingDTO);
    }

    @Test
    @DisplayName("Should book multiple adjacent seats")
    void testBookMultipleAdjacentSeats() throws Exception {
        validBookingDTO.setNumberOfTickets(3L);
        validBookingDTO.setSeatNumbers(Arrays.asList("B1", "B2", "B3"));
        when(ticketService.bookTicket(validBookingDTO)).thenReturn(testTicket);

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(ticketService, times(1)).bookTicket(validBookingDTO);
    }

    @Test
    @DisplayName("Should book single seat")
    void testBookSingleSeat() throws Exception {
        validBookingDTO.setNumberOfTickets(1L);
        validBookingDTO.setSeatNumbers(Arrays.asList("C5"));
        when(ticketService.bookTicket(validBookingDTO)).thenReturn(testTicket);

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ===== BOOK TICKET - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail to book already booked seats")
    void testBookTicketAlreadyBooked() throws Exception {
        when(ticketService.bookTicket(validBookingDTO))
            .thenThrow(new Exception("Seat A1 is already booked."));

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("already booked"));
    }

    @Test
    @DisplayName("Should fail to book non-adjacent seats")
    void testBookTicketNonAdjacentSeats() throws Exception {
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "A3")); // Non-adjacent
        when(ticketService.bookTicket(validBookingDTO))
            .thenThrow(new Exception("Seats must be adjacent in the same row"));

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("adjacent"));
    }

    @Test
    @DisplayName("Should fail when seats count doesn't match ticket count")
    void testBookTicketCountMismatch() throws Exception {
        validBookingDTO.setNumberOfTickets(3L);
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "A2")); // Only 2 seats for 3 tickets
        when(ticketService.bookTicket(validBookingDTO))
            .thenThrow(new Exception("Number of tickets does not match"));

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should fail when movie doesn't exist")
    void testBookTicketMovieNotFound() throws Exception {
        validBookingDTO.setMovieName("NonExistent");
        when(ticketService.bookTicket(validBookingDTO))
            .thenThrow(new Exception("Movie not found"));

        ResponseEntity<?> response = ticketController.bookTicket("NonExistent", validBookingDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should fail when no seats available")
    void testBookTicketNoSeatsAvailable() throws Exception {
        when(ticketService.bookTicket(validBookingDTO))
            .thenThrow(new Exception("Only 0 tickets are available"));

        ResponseEntity<?> response = ticketController.bookTicket("Inception", validBookingDTO);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ===== GET MY TICKETS - POSITIVE TESTS =====
    @Test
    @DisplayName("Should return user's tickets")
    void testGetMyTicketsSuccess() throws Exception {
        List<Ticket> userTickets = Arrays.asList(testTicket);
        when(ticketService.getTicketsByUsername("johndoe")).thenReturn(userTickets);

        ResponseEntity<?> response = ticketController.getMyTickets("johndoe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userTickets, response.getBody());
        verify(ticketService, times(1)).getTicketsByUsername("johndoe");
    }

    @Test
    @DisplayName("Should return multiple booked tickets for user")
    void testGetMyTicketsMultiple() throws Exception {
        Ticket ticket2 = new Ticket();
        ticket2.setTransactionId(2L);
        ticket2.setNumberOfTickets(1L);
        ticket2.setSeatNumbers(Arrays.asList("D3"));
        
        List<Ticket> userTickets = Arrays.asList(testTicket, ticket2);
        when(ticketService.getTicketsByUsername("johndoe")).thenReturn(userTickets);

        ResponseEntity<?> response = ticketController.getMyTickets("johndoe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<?>) response.getBody()).size());
    }

    // ===== GET MY TICKETS - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should return empty list for user with no tickets")
    void testGetMyTicketsEmpty() throws Exception {
        when(ticketService.getTicketsByUsername("newuser")).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = ticketController.getMyTickets("newuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, ((List<?>) response.getBody()).size());
    }

    // ===== GET BOOKED SEATS - POSITIVE TESTS =====
    @Test
    @DisplayName("Should return booked seats for a movie")
    void testGetBookedSeatsSuccess() throws Exception {
        List<String> bookedSeats = Arrays.asList("A1", "A2", "B1", "B2");
        when(ticketService.getBookedSeats("Inception", "PVR Cinema")).thenReturn(bookedSeats);

        ResponseEntity<?> response = ticketController.getBookedSeats("Inception", "PVR Cinema");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4, ((List<?>) response.getBody()).size());
        verify(ticketService, times(1)).getBookedSeats("Inception", "PVR Cinema");
    }

    // ===== GET BOOKED SEATS - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should return empty list when no seats booked")
    void testGetBookedSeatsEmpty() throws Exception {
        when(ticketService.getBookedSeats("Avatar", "INOX")).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = ticketController.getBookedSeats("Avatar", "INOX");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, ((List<?>) response.getBody()).size());
    }
}
