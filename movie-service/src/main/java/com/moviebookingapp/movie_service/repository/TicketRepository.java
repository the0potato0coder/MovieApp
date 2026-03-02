package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // US_04: View booked tickets for a particular movie
    @Query("SELECT t FROM Ticket t WHERE t.movie.id.movieName = :movieName")
    List<Ticket> findByMovieName(@Param("movieName") String movieName);

    // US_04: Calculate the total tickets booked for a specific movie at a specific theatre
    @Query("SELECT SUM(t.numberOfTickets) FROM Ticket t WHERE t.movie.id.movieName = :movieName AND t.movie.id.theatreName = :theatreName")
    Integer sumTicketsBookedForMovieAndTheatre(@Param("movieName") String movieName, @Param("theatreName") String theatreName);

    // US_05: Find tickets by username
    @Query("SELECT t FROM Ticket t WHERE t.username = :username")
    List<Ticket> findByUsername(@Param("username") String username);

    // US_06: Find tickets by movie and theatre for seat validation
    @Query("SELECT t FROM Ticket t WHERE t.movie.id.movieName = :movieName AND t.movie.id.theatreName = :theatreName")
    List<Ticket> findByMovieNameAndTheatreName(@Param("movieName") String movieName, @Param("theatreName") String theatreName);
}
