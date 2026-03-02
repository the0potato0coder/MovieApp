package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Ticket Service Tests")
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private TicketBookingDTO validBookingDTO;
    private Movie testMovie;
    private MovieTheatreKey movieKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        movieKey = new MovieTheatreKey("PVR Cinema", "Inception");
        testMovie = new Movie();
        testMovie.setId(movieKey);
        testMovie.setTotalTicketsAllotted(100);
        testMovie.setTicketStatus("AVAILABLE");

        validBookingDTO = new TicketBookingDTO();
        validBookingDTO.setMovieName("Inception");
        validBookingDTO.setTheatreName("PVR Cinema");
        validBookingDTO.setNumberOfTickets(2L);
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "A2"));
        validBookingDTO.setUsername("johndoe");
    }

    // ===== BOOK TICKET - POSITIVE TESTS =====
    @Test
    @DisplayName("Should successfully book a ticket with adjacent seats")
    void testBookTicketSuccess() throws Exception {
        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.findById(expectedKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(0);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema")).thenReturn(new ArrayList<>());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(createTestTicket());

        Ticket result = ticketService.bookTicket(validBookingDTO);

        assertNotNull(result);
        assertEquals(2L, result.getNumberOfTickets());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Should book single seat successfully")
    void testBookSingleSeatSuccess() throws Exception {
        validBookingDTO.setNumberOfTickets(1L);
        validBookingDTO.setSeatNumbers(Arrays.asList("B5"));

        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.findById(expectedKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(0);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema")).thenReturn(new ArrayList<>());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(createTestTicket());

        Ticket result = ticketService.bookTicket(validBookingDTO);

        assertNotNull(result);
        assertEquals(1L, result.getNumberOfTickets());
    }

    @Test
    @DisplayName("Should book multiple adjacent seats across rows")
    void testBookMultipleSeatsAcrossRows() throws Exception {
        validBookingDTO.setNumberOfTickets(3L);
        validBookingDTO.setSeatNumbers(Arrays.asList("A9", "A10", "B1"));

        MovieTheatreKey expectedKey = new MovieTheatreKey("PVR Cinema", "Inception");
        when(movieRepository.findById(expectedKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(0);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema")).thenReturn(new ArrayList<>());
        when(ticketRepository.save(any(Ticket.class))).thenReturn(createTestTicket());

        Ticket result = ticketService.bookTicket(validBookingDTO);

        assertNotNull(result);
    }

    // ===== BOOK TICKET - NEGATIVE TESTS =====
    @Test
    @DisplayName("Should fail when seats count doesn't match tickets count")
    void testBookTicketCountMismatch() {
        validBookingDTO.setNumberOfTickets(3L);
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "A2"));

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    @Test
    @DisplayName("Should fail when movie doesn't exist")
    void testBookTicketMovieNotFound() {
        when(movieRepository.findById(movieKey)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    @Test
    @DisplayName("Should fail when no seats available")
    void testBookTicketNoSeatsAvailable() {
        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(99);

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    @Test
    @DisplayName("Should fail when requesting more tickets than available")
    void testBookTicketInsufficientSeats() {
        testMovie.setTotalTicketsAllotted(50);
        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(49);

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    @Test
    @DisplayName("Should fail when seats are already booked")
    void testBookTicketAlreadyBooked() {
        Ticket existingTicket = new Ticket();
        existingTicket.setNumberOfTickets(1L);
        existingTicket.setSeatNumbers(Arrays.asList("A1"));

        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(1);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema"))
            .thenReturn(Arrays.asList(existingTicket));

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    @Test
    @DisplayName("Should fail when seats are not adjacent in same row")
    void testBookTicketNonAdjacentSeats() {
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "A3")); // Non-adjacent

        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(0);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema")).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    @Test
    @DisplayName("Should fail when seats are from different rows")
    void testBookTicketDifferentRows() {
        validBookingDTO.setSeatNumbers(Arrays.asList("A1", "B1")); // Different rows

        when(movieRepository.findById(movieKey)).thenReturn(Optional.of(testMovie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema")).thenReturn(0);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema")).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> ticketService.bookTicket(validBookingDTO));
    }

    // ===== GET BOOKED TICKETS - POSITIVE TEST =====
    @Test
    @DisplayName("Should return booked tickets for a movie")
    void testGetBookedTicketsSuccess() {
        List<Ticket> tickets = Arrays.asList(createTestTicket());
        when(ticketRepository.findByMovieName("Inception")).thenReturn(tickets);

        List<Ticket> result = ticketService.getBookedTickets("Inception");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByMovieName("Inception");
    }

    // ===== GET BOOKED TICKETS - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when no tickets booked")
    void testGetBookedTicketsEmpty() {
        when(ticketRepository.findByMovieName("Avatar")).thenReturn(new ArrayList<>());

        List<Ticket> result = ticketService.getBookedTickets("Avatar");

        assertTrue(result.isEmpty());
    }

    // ===== GET TICKETS BY USERNAME - POSITIVE TEST =====
    @Test
    @DisplayName("Should return tickets for a specific user")
    void testGetTicketsByUsernameSuccess() {
        List<Ticket> tickets = Arrays.asList(createTestTicket());
        when(ticketRepository.findByUsername("johndoe")).thenReturn(tickets);

        List<Ticket> result = ticketService.getTicketsByUsername("johndoe");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByUsername("johndoe");
    }

    // ===== GET TICKETS BY USERNAME - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list for user with no tickets")
    void testGetTicketsByUsernameEmpty() {
        when(ticketRepository.findByUsername("newuser")).thenReturn(new ArrayList<>());

        List<Ticket> result = ticketService.getTicketsByUsername("newuser");

        assertTrue(result.isEmpty());
    }

    // ===== GET BOOKED SEATS - POSITIVE TESTS =====
    @Test
    @DisplayName("Should return booked seats for a movie in a theatre")
    void testGetBookedSeatsSuccess() {
        Ticket ticket1 = new Ticket();
        ticket1.setSeatNumbers(Arrays.asList("A1", "A2"));
        Ticket ticket2 = new Ticket();
        ticket2.setSeatNumbers(Arrays.asList("B1"));

        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema"))
            .thenReturn(Arrays.asList(ticket1, ticket2));

        List<String> result = ticketService.getBookedSeats("Inception", "PVR Cinema");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("A1"));
        assertTrue(result.contains("A2"));
        assertTrue(result.contains("B1"));
    }

    // ===== GET BOOKED SEATS - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when no seats booked")
    void testGetBookedSeatsEmpty() {
        when(ticketRepository.findByMovieNameAndTheatreName("Avatar", "INOX"))
            .thenReturn(new ArrayList<>());

        List<String> result = ticketService.getBookedSeats("Avatar", "INOX");

        assertTrue(result.isEmpty());
    }

    // ===== GET ALL MOVIES - POSITIVE TEST =====
    @Test
    @DisplayName("Should return all movies")
    void testGetAllMoviesSuccess() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = ticketService.getAllMovies();

        assertEquals(1, result.size());
    }

    // Helper method to create test ticket
    private Ticket createTestTicket() {
        Ticket ticket = new Ticket();
        ticket.setTransactionId(1L);
        ticket.setMovie(testMovie);
        ticket.setNumberOfTickets(2L);
        ticket.setSeatNumbers(Arrays.asList("A1", "A2"));
        ticket.setUsername("johndoe");
        return ticket;
    }
}
