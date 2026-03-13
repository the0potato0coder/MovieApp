package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.service.MovieService;
import com.moviebookingapp.movie_service.service.TicketService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    MovieService movieService;
    @Mock
    TicketService ticketService;

    @InjectMocks
    AdminController adminController;

    @Test
    void viewBookedTickets_returnsNoContent_whenEmpty() {
        when(ticketService.getBookedTickets(anyString())).thenReturn(Collections.emptyList());
        ResponseEntity<List<Ticket>> response = adminController.viewBookedTickets("movie");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void viewBookedTickets_returnsOk_whenTicketsPresent() {
        Ticket ticket = new Ticket();
        when(ticketService.getBookedTickets(anyString())).thenReturn(List.of(ticket));
        ResponseEntity<List<Ticket>> response = adminController.viewBookedTickets("movie");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void updateTicketStatus_returnsOk_whenSuccess() throws Exception {
        Movie updatedMovie = new Movie();
        when(movieService.updateMovieTickets(anyString(), anyString(), anyInt())).thenReturn(updatedMovie);
        ResponseEntity<?> response = adminController.updateTicketStatus("movie", "theatre", 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedMovie, response.getBody());
    }

    @Test
    void updateTicketStatus_returnsNotFound_whenExceptionThrown() throws Exception {
        when(movieService.updateMovieTickets(anyString(), anyString(), anyInt())).thenThrow(new Exception("Not found"));
        ResponseEntity<?> response = adminController.updateTicketStatus("movie", "theatre", 10);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Not found"));
    }

    @Test
    void updateTicketStatusDirect_returnsOk_whenSuccess() throws Exception {
        Movie updatedMovie = new Movie();
        when(movieService.updateTicketStatusDirect(anyString(), anyString(), anyString())).thenReturn(updatedMovie);
        ResponseEntity<?> response = adminController.updateTicketStatusDirect("movie", "theatre", "CONFIRMED");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedMovie, response.getBody());
    }

    @Test
    void updateTicketStatusDirect_returnsBadRequest_whenExceptionThrown() throws Exception {
        when(movieService.updateTicketStatusDirect(anyString(), anyString(), anyString())).thenThrow(new Exception("Bad request"));
        ResponseEntity<?> response = adminController.updateTicketStatusDirect("movie", "theatre", "INVALID");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Bad request"));
    }

    @Test
    void addMovie_returnsCreated_whenSuccess() {
        Movie movie = new Movie();
        when(movieService.addMovie(any(Movie.class))).thenReturn(movie);
        ResponseEntity<?> response = adminController.addMovie(movie);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(movie, response.getBody());
    }

    @Test
    void addMovie_returnsBadRequest_whenExceptionThrown() {
        Movie movieObj = new Movie();
        when(movieService.addMovie(any(Movie.class))).thenThrow(new RuntimeException("Bad request"));
        ResponseEntity<?> badResponse = adminController.addMovie(movieObj);
        assertEquals(HttpStatus.BAD_REQUEST, badResponse.getStatusCode());
        assertTrue(badResponse.getBody().toString().contains("Bad request"));
    }
}
