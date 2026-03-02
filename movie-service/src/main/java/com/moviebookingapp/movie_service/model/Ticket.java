package com.moviebookingapp.movie_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.moviebookingapp.movie_service.model.Movie;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "movie_name", referencedColumnName = "movieName"),
            @JoinColumn(name = "theatre_name", referencedColumnName = "theatreName")
    })
    private Movie movie;

    private Long numberOfTickets;

    @ElementCollection
    @CollectionTable(name = "ticket_seats", joinColumns = @JoinColumn(name = "transaction_id"))
    @Column(name = "seat_number")
    private List<String> seatNumbers;

    // Add username field for ticket owner
    private String username;

    public Ticket(Movie movie, Long numberOfTickets, List<String> seatNumbers) {
        this.movie = movie;
        this.numberOfTickets = numberOfTickets;
        this.seatNumbers = seatNumbers;
    }

    public Ticket(Movie movie, Long numberOfTickets, List<String> seatNumbers, String username) {
        this.movie = movie;
        this.numberOfTickets = numberOfTickets;
        this.seatNumbers = seatNumbers;
        this.username = username;
    }
}
