package com.moviebookingapp.movie_service.service;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.repository.MovieRepository;
import com.moviebookingapp.movie_service.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceImplTest {

    @Mock
    TicketRepository ticketRepository;
    @Mock
    MovieRepository movieRepository;

    @InjectMocks
    TicketServiceImpl ticketService;

    @Test
    void bookTicket_successful() throws Exception {
        TicketBookingDTO dto = new TicketBookingDTO();
        dto.setMovieName("movie");
        dto.setTheatreName("theatre");
        dto.setNumberOfTickets(2L);
        dto.setSeatNumbers(java.util.List.of("A1", "A2")); // adjacent seats
        dto.setUsername("user");
        Movie movie = new Movie();
        movie.setTotalTicketsAllotted(10);
        MovieTheatreKey key = new MovieTheatreKey("theatre", "movie");
        movie.setId(key);
        when(movieRepository.findById(key)).thenReturn(Optional.of(movie));
        when(ticketRepository.sumTicketsBookedForMovieAndTheatre("movie", "theatre")).thenReturn(0);
        when(ticketRepository.findByMovieNameAndTheatreName("movie", "theatre")).thenReturn(java.util.Collections.emptyList());
        Ticket ticket = new Ticket(movie, 2L, java.util.List.of("A1", "A2"), "user");
        when(ticketRepository.save(any())).thenReturn(ticket);
        Ticket result = ticketService.bookTicket(dto);
        assertNotNull(result);
        assertEquals(movie, result.getMovie());
        assertEquals(2L, result.getNumberOfTickets());
        assertEquals(java.util.List.of("A1", "A2"), result.getSeatNumbers());
        assertEquals("user", result.getUsername());
    }

    @Test
    void bookTicket_throwsException_whenMovieNotFound() {
        TicketBookingDTO dto = new TicketBookingDTO();
        dto.setMovieName("movie");
        dto.setTheatreName("theatre");
        dto.setNumberOfTickets(1L);
        dto.setSeatNumbers(java.util.List.of("A1"));
        dto.setUsername("user");
        MovieTheatreKey key = new MovieTheatreKey("theatre", "movie");
        when(movieRepository.findById(key)).thenReturn(Optional.empty());
        Exception ex = assertThrows(Exception.class, () -> ticketService.bookTicket(dto));
        assertTrue(ex.getMessage().contains("Movie not found"));
    }

    @Test
    void bookTicket_throwsException_whenTicketCountMismatch() {
        TicketBookingDTO dto = new TicketBookingDTO();
        dto.setMovieName("movie");
        dto.setTheatreName("theatre");
        dto.setNumberOfTickets(2L);
        dto.setSeatNumbers(java.util.List.of("A1"));
        dto.setUsername("user");
        Exception ex = assertThrows(Exception.class, () -> ticketService.bookTicket(dto));
        assertTrue(ex.getMessage().contains("Number of tickets does not match"));
    }
}
