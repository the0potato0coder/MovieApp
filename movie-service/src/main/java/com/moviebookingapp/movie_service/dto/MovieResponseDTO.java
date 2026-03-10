package com.moviebookingapp.movie_service.dto;

import com.moviebookingapp.movie_service.model.Movie;
import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponseDTO {

    private MovieTheatreKey id;
    private int totalTicketsAlloted;
    private String ticketStatus;
    private int availableTickets;

    public static MovieResponseDTO fromMovie(Movie movie, int bookedTickets) {
        MovieResponseDTO dto = new MovieResponseDTO();
        dto.setId(movie.getId());
        dto.setTotalTicketsAlloted(movie.getTotalTicketsAlloted());
        dto.setTicketStatus(movie.getTicketStatus());
        int available = movie.getTotalTicketsAlloted() - bookedTickets;
        dto.setAvailableTickets("SOLD OUT".equals(movie.getTicketStatus()) ? 0 : Math.max(available, 0));
        return dto;
    }
}
