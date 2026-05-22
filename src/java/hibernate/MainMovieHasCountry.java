package hibernate;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "main_movie_has_country")
public class MainMovieHasCountry implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "main_movie_id")
    private MainMovie mainMovie;

    @Id
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public MainMovieHasCountry() {
    }

    public MainMovie getMainMovie() {
        return mainMovie;
    }

    public void setMainMovie(MainMovie mainMovie) {
        this.mainMovie = mainMovie;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

}
