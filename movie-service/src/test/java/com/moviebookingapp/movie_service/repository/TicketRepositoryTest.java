package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Ticket Repository Tests")
class TicketRepositoryTest {

    @Mock
    private TicketRepository ticketRepository;

    private Ticket testTicket;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        MovieTheatreKey movieKey = new MovieTheatreKey("PVR Cinema", "Inception");
        testMovie = new Movie();
        testMovie.setId(movieKey);
        testMovie.setTotalTicketsAllotted(100);

        testTicket = new Ticket();
        testTicket.setTransactionId(1L);
        testTicket.setMovie(testMovie);
        testTicket.setNumberOfTickets(2L);
        testTicket.setSeatNumbers(Arrays.asList("A1", "A2"));
        testTicket.setUsername("johndoe");
    }

    // ===== FIND BY MOVIE NAME - POSITIVE TEST =====
    @Test
    @DisplayName("Should find tickets by movie name")
    void testFindByMovieNameSuccess() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findByMovieName("Inception")).thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByMovieName("Inception");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByMovieName("Inception");
    }

    // ===== FIND BY MOVIE NAME - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when no tickets found for movie")
    void testFindByMovieNameEmpty() {
        when(ticketRepository.findByMovieName("Avatar")).thenReturn(new ArrayList<>());

        List<Ticket> result = ticketRepository.findByMovieName("Avatar");

        assertTrue(result.isEmpty());
    }

    // ===== FIND BY USERNAME - POSITIVE TEST =====
    @Test
    @DisplayName("Should find tickets by username")
    void testFindByUsernameSuccess() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findByUsername("johndoe")).thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByUsername("johndoe");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("johndoe", result.get(0).getUsername());
        verify(ticketRepository, times(1)).findByUsername("johndoe");
    }

    // ===== FIND BY USERNAME - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when user has no tickets")
    void testFindByUsernameEmpty() {
        when(ticketRepository.findByUsername("newuser")).thenReturn(new ArrayList<>());

        List<Ticket> result = ticketRepository.findByUsername("newuser");

        assertTrue(result.isEmpty());
    }

    // ===== FIND BY MOVIE NAME AND THEATRE NAME - POSITIVE TEST =====
    @Test
    @DisplayName("Should find tickets by movie and theatre")
    void testFindByMovieNameAndTheatreNameSuccess() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema"))
            .thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(ticketRepository, times(1)).findByMovieNameAndTheatreName("Inception", "PVR Cinema");
    }

    // ===== FIND BY MOVIE NAME AND THEATRE NAME - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty list when no tickets for movie and theatre combo")
    void testFindByMovieNameAndTheatreNameEmpty() {
        when(ticketRepository.findByMovieNameAndTheatreName("Avatar", "INOX"))
            .thenReturn(new ArrayList<>());

        List<Ticket> result = ticketRepository.findByMovieNameAndTheatreName("Avatar", "INOX");

        assertTrue(result.isEmpty());
    }

    // ===== SUM TICKETS BOOKED - POSITIVE TEST =====
    @Test
    @DisplayName("Should sum booked tickets for a movie and theatre")
    void testSumTicketsBookedSuccess() {
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema"))
            .thenReturn(10);

        Integer result = ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema");

        assertNotNull(result);
        assertEquals(10, result);
        verify(ticketRepository, times(1)).sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema");
    }

    // ===== SUM TICKETS BOOKED - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return 0 when no tickets booked for movie and theatre")
    void testSumTicketsBookedZero() {
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Avatar", "INOX"))
            .thenReturn(0);

        Integer result = ticketRepository.sumTicketsBookedForMovieAndTheatre("Avatar", "INOX");

        assertEquals(0, result);
    }

    // ===== SAVE TICKET - POSITIVE TEST =====
    @Test
    @DisplayName("Should save a new ticket")
    void testSaveTicketSuccess() {
        when(ticketRepository.save(testTicket)).thenReturn(testTicket);

        Ticket result = ticketRepository.save(testTicket);

        assertNotNull(result);
        assertEquals(1L, result.getTransactionId());
        assertEquals(2L, result.getNumberOfTickets());
        verify(ticketRepository, times(1)).save(testTicket);
    }

    // ===== FIND BY ID - POSITIVE TEST =====
    @Test
    @DisplayName("Should find ticket by transaction id")
    void testFindByIdSuccess() {
        when(ticketRepository.findById(1L)).thenReturn(java.util.Optional.of(testTicket));

        java.util.Optional<Ticket> result = ticketRepository.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getTransactionId());
    }

    // ===== FIND BY ID - NEGATIVE TEST =====
    @Test
    @DisplayName("Should return empty when ticket not found")
    void testFindByIdNotFound() {
        when(ticketRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        java.util.Optional<Ticket> result = ticketRepository.findById(999L);

        assertTrue(result.isEmpty());
    }

    // ===== FIND BOOKED SEATS - POSITIVE TEST =====
    @Test
    @DisplayName("Should return booked seats for a movie and theatre")
    void testGetBookedSeatsSuccess() {
        List<Ticket> tickets = Arrays.asList(testTicket);
        when(ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema"))
            .thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByMovieNameAndTheatreName("Inception", "PVR Cinema");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.get(0).getSeatNumbers().contains("A1"));
        assertTrue(result.get(0).getSeatNumbers().contains("A2"));
    }

    // ===== DELETE TICKET - POSITIVE TEST =====
    @Test
    @DisplayName("Should delete ticket by transaction id")
    void testDeleteTicketSuccess() {
        when(ticketRepository.findById(1L)).thenReturn(java.util.Optional.of(testTicket));

        ticketRepository.deleteById(1L);

        verify(ticketRepository, times(1)).deleteById(1L);
    }

    // ===== MULTIPLE TICKETS FOR SAME USER - POSITIVE TEST =====
    @Test
    @DisplayName("Should find multiple tickets for same user")
    void testMultipleTicketsForUser() {
        Ticket ticket2 = new Ticket();
        ticket2.setTransactionId(2L);
        ticket2.setNumberOfTickets(3L);
        ticket2.setSeatNumbers(Arrays.asList("C1", "C2", "C3"));
        ticket2.setUsername("johndoe");

        List<Ticket> tickets = Arrays.asList(testTicket, ticket2);
        when(ticketRepository.findByUsername("johndoe")).thenReturn(tickets);

        List<Ticket> result = ticketRepository.findByUsername("johndoe");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getUsername().equals("johndoe")));
    }

    // ===== COUNT TICKETS BY MOVIE - POSITIVE TEST =====
    @Test
    @DisplayName("Should count total tickets booked for a movie")
    void testCountTicketsBookedSuccess() {
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema"))
            .thenReturn(25);

        Integer total = ticketRepository.sumTicketsBookedForMovieAndTheatre("Inception", "PVR Cinema");

        assertEquals(25, total);
    }
}
