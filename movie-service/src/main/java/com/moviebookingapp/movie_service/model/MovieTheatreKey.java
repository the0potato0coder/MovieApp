package com.moviebookingapp.movie_service.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MovieTheatreKey implements Serializable {

    private String theatreName;
    private String movieName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieTheatreKey that = (MovieTheatreKey) o;
        return Objects.equals(theatreName, that.theatreName) &&
                Objects.equals(movieName, that.movieName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theatreName, movieName);
    }
}
