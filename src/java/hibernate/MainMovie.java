package hibernate;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "main_movie")
public class MainMovie implements Serializable {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "production", nullable = false)
    private String production;

    @Column(name = "cast", nullable = false)
    private String cast;

    @Column(name = "released_at", nullable = false)
    private Date released_at;

    @Column(name = "trailer", nullable = false)
    private String trailer;

    @ManyToOne
    @JoinColumn(name = "quality_type_id")
    private QualityType qualityType;

    @ManyToOne
    @JoinColumn(name = "movie_type_id")
    private MovieType movieType;

    @Column(name = "duration", length = 25, nullable = false)
    private String duration;

    @Column(name = "episode_count", nullable = false)
    private int episode_count;

    @Column(name = "registered_at", nullable = false)
    private Date registered_at;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "rating", nullable = false)
    private double rating;

    @Column(name = "selling_count", nullable = false)
    private int selling_count;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public MainMovie() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProduction() {
        return production;
    }

    public void setProduction(String production) {
        this.production = production;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public Date getReleased_at() {
        return released_at;
    }

    public void setReleased_at(Date released_at) {
        this.released_at = released_at;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public QualityType getQualityType() {
        return qualityType;
    }

    public void setQualityType(QualityType qualityType) {
        this.qualityType = qualityType;
    }

    public MovieType getMovieType() {
        return movieType;
    }

    public void setMovieType(MovieType movieType) {
        this.movieType = movieType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getEpisode_count() {
        return episode_count;
    }

    public void setEpisode_count(int episode_count) {
        this.episode_count = episode_count;
    }

    public Date getRegistered_at() {
        return registered_at;
    }

    public void setRegistered_at(Date registered_at) {
        this.registered_at = registered_at;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getSelling_count() {
        return selling_count;
    }

    public void setSelling_count(int selling_count) {
        this.selling_count = selling_count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
