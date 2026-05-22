package hibernate;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "main_movie_has_genre")
public class MainMovieHasGenre implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "main_movie_id")
    private MainMovie mainMovie;

    @Id
    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    public MainMovieHasGenre() {
    }

    public MainMovie getMainMovie() {
        return mainMovie;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setMainMovie(MainMovie mainMovie) {
        this.mainMovie = mainMovie;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

}
