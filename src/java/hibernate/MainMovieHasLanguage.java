package hibernate;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "main_movie_has_language")
public class MainMovieHasLanguage implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "main_movie_id")
    private MainMovie mainMovie;

    @Id
    @ManyToOne
    @JoinColumn(name = "language_id")
    private Language language;

    public MainMovieHasLanguage() {
    }

    public MainMovie getMainMovie() {
        return mainMovie;
    }

    public Language getLanguage() {
        return language;
    }

    public void setMainMovie(MainMovie mainMovie) {
        this.mainMovie = mainMovie;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

}
