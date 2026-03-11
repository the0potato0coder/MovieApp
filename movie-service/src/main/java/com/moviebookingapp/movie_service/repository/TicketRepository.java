package com.moviebookingapp.movie_service.repository;

import com.moviebookingapp.movie_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.movie.id.movieName = :movieName")
    List<Ticket> findByMovieName(@Param("movieName") String movieName);

    @Query("SELECT SUM(t.numberOfTickets) FROM Ticket t WHERE t.movie.id.movieName = :movieName AND t.movie.id.theatreName = :theatreName")
    Integer sumTicketsBookedForMovieAndTheatre(@Param("movieName") String movieName, @Param("theatreName") String theatreName);

    @Query("SELECT t FROM Ticket t WHERE t.username = :username")
    List<Ticket> findByUsername(@Param("username") String username);

    @Query("SELECT t FROM Ticket t WHERE t.movie.id.movieName = :movieName AND t.movie.id.theatreName = :theatreName")
    List<Ticket> findByMovieNameAndTheatreName(@Param("movieName") String movieName, @Param("theatreName") String theatreName);
}
