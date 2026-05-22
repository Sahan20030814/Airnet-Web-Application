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
@Table(name = "checkout_items")
public class CheckoutItems implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "checkout_id")
    private Checkout checkout;

    @Id
    @ManyToOne
    @JoinColumn(name = "main_movie_id")
    private MainMovie mainMovie;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "rate", nullable = false)
    private double rate;

    @Column(name = "owner_price", nullable = false)
    private double owner_price;

    @Column(name = "site_price", nullable = false)
    private double site_price;

    @Column(name = "registered_at", nullable = false)
    private Date registered_at;

    @ManyToOne
    @JoinColumn(name = "rating_type_id", nullable = false)
    private RatingType ratingType;

    public CheckoutItems() {
    }

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

    public MainMovie getMainMovie() {
        return mainMovie;
    }

    public void setMainMovie(MainMovie mainMovie) {
        this.mainMovie = mainMovie;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getOwner_price() {
        return owner_price;
    }

    public void setOwner_price(double owner_price) {
        this.owner_price = owner_price;
    }

    public double getSite_price() {
        return site_price;
    }

    public void setSite_price(double site_price) {
        this.site_price = site_price;
    }

    public Date getRegistered_at() {
        return registered_at;
    }

    public void setRegistered_at(Date registered_at) {
        this.registered_at = registered_at;
    }

    public RatingType getRatingType() {
        return ratingType;
    }

    public void setRatingType(RatingType ratingType) {
        this.ratingType = ratingType;
    }

}
