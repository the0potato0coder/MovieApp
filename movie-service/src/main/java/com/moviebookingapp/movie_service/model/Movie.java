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

    private String ticketStatus;

    public int getTotalTicketsAllotted() {
        return totalTicketsAlloted;
    }

    public void setTotalTicketsAllotted(int newTotalTickets) {
        this.totalTicketsAlloted = newTotalTickets;
    }

    public String getMovieName() {
        return id != null ? id.getMovieName() : null;
    }

    public String getTheatreName() {
        return id != null ? id.getTheatreName() : null;
    }
}