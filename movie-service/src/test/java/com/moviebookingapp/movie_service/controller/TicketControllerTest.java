package com.moviebookingapp.movie_service.controller;

import com.moviebookingapp.movie_service.dto.TicketBookingDTO;
import com.moviebookingapp.movie_service.model.Ticket;
import com.moviebookingapp.movie_service.service.TicketService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @Mock
    TicketService ticketService;

    @InjectMocks
    TicketController ticketController;

    @Test
    void bookTicket_returnsOk_whenSuccess() throws Exception {
        TicketBookingDTO dto = new TicketBookingDTO();
        dto.setMovieName("movie");
        Ticket ticket = new Ticket();
        doReturn(ticket).when(ticketService).bookTicket(dto);
        ResponseEntity<?> response = ticketController.bookTicket("movie", dto);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ticket, response.getBody());
    }

    @Test
    void bookTicket_returnsForbidden_whenException() throws Exception {
        TicketBookingDTO dto = new TicketBookingDTO();
        dto.setMovieName("movie");
        doThrow(new Exception("fail")).when(ticketService).bookTicket(dto);
        ResponseEntity<?> response = ticketController.bookTicket("movie", dto);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("fail", response.getBody());
    }
}
