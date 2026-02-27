package com.moviebookingapp.movie_service.model;

import com.moviebookingapp.movie_service.model.MovieTheatreKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "movie")
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    @EmbeddedId
    private MovieTheatreKey id;

    private int totalTicketsAlloted;

    private String ticketStatus; // Will hold "SOLD OUT", "BOOK ASAP", or "AVAILABLE"

    public int getTotalTicketsAllotted() {
        return totalTicketsAlloted;
    }

    public void setTotalTicketsAllotted(int newTotalTickets) {
    }
}