package com.moviebookingapp.movie_service.dto;

import java.util.List;

public class TicketBookingDTO {
    private String movieName;
    private String theatreName;
    private Long numberOfTickets;
    private List<String> seatNumbers;
    private String username;

    public TicketBookingDTO() {}

    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }

    public String getTheatreName() { return theatreName; }
    public void setTheatreName(String theatreName) { this.theatreName = theatreName; }

    public Long getNumberOfTickets() { return numberOfTickets; }
    public void setNumberOfTickets(Long numberOfTickets) { this.numberOfTickets = numberOfTickets; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}