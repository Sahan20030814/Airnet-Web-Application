package hibernate;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "watchlist")
public class Watchlist implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "main_movie_id")
    private MainMovie mainMovie;

    public Watchlist() {
    }

    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MainMovie getMainMovie() {
        return mainMovie;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMainMovie(MainMovie mainMovie) {
        this.mainMovie = mainMovie;
    }

}
