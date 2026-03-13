package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import com.moviebookingapp.movie_service.model.Ticket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketRepositoryTest {

    @Mock
    TicketRepository ticketRepository;
    @Mock
    MovieRepository movieRepository;

    @Test
    void findByUsername_returnsTickets_withMockito() {
        MovieTheatreKey key = new MovieTheatreKey("theatre", "movie");
        Movie movie = new Movie();
        movie.setId(key);
        movie.setTotalTicketsAllotted(10);
        Ticket ticket = new Ticket(movie, 1L, List.of("A1"), "user1");
        when(ticketRepository.findByUsername("user1")).thenReturn(List.of(ticket));
        List<Ticket> found = ticketRepository.findByUsername("user1");
        assertFalse(found.isEmpty());
        assertEquals("user1", found.get(0).getUsername());
        verify(ticketRepository, times(1)).findByUsername("user1");
    }

    @Test
    void findByUsername_returnsEmpty_withMockito() {
        when(ticketRepository.findByUsername("nouser")).thenReturn(List.of());
        List<Ticket> found = ticketRepository.findByUsername("nouser");
        assertTrue(found.isEmpty());
        verify(ticketRepository, times(1)).findByUsername("nouser");
    }
}
