/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Checkout;
import hibernate.CheckoutItems;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.MainMovieHasCountry;
import hibernate.MainMovieHasGenre;
import hibernate.MainMovieHasLanguage;
import hibernate.RatingType;
import hibernate.Status;
import hibernate.User;
import hibernate.Watchlist;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        responseObject.addProperty("contentCheckoutStatus", false);
        responseObject.addProperty("contentRatingStatus", false);

        String contentId = request.getParameter("id");

        if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            try {
                MainMovie mainMovie = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
                if (mainMovie != null) {
                    boolean canContinue = false;
                    HttpSession ses = request.getSession(false);

                    if (mainMovie.getStatus().getName().equalsIgnoreCase("Active")) {
                        canContinue = true;

                        if (ses != null && ses.getAttribute("user") != null) {
                            User user = (User) ses.getAttribute("user");

                            if (user.getId() == mainMovie.getUser().getId()) {
                                responseObject.addProperty("contentCheckoutStatus", true);
                                responseObject.addProperty("contentRatingType", 0);
                            } else {
                                Criteria c1 = session.createCriteria(Checkout.class);
                                c1.add(Restrictions.eq("user", user));
                                List<Checkout> checkoutList = c1.list();

                                for (Checkout checkout : checkoutList) {
                                    Criteria c2 = session.createCriteria(CheckoutItems.class);
                                    c2.add(Restrictions.eq("checkout", checkout));
                                    c2.add(Restrictions.eq("mainMovie", mainMovie));
                                    CheckoutItems checkoutItems = (CheckoutItems) c2.uniqueResult();
                                    if (checkoutItems != null) {
                                        responseObject.addProperty("contentCheckoutStatus", true);
                                        responseObject.addProperty("contentRatingStatus", true);
                                        responseObject.addProperty("contentRatingType", checkoutItems.getRatingType().getId());
                                        break;
                                    }
                                }
                            }
                        }

                    } else {

                        if (ses != null && ses.getAttribute("user") != null) {
                            User user = (User) ses.getAttribute("user");

                            if (user.getId() == mainMovie.getUser().getId()) {
                                responseObject.addProperty("contentCheckoutStatus", true);
                                responseObject.addProperty("contentRatingType", 0);
                            } else {
                                Criteria c1 = session.createCriteria(Checkout.class);
                                c1.add(Restrictions.eq("user", user));
                                List<Checkout> checkoutList = c1.list();

                                for (Checkout checkout : checkoutList) {
                                    Criteria c2 = session.createCriteria(CheckoutItems.class);
                                    c2.add(Restrictions.eq("checkout", checkout));
                                    c2.add(Restrictions.eq("mainMovie", mainMovie));
                                    CheckoutItems checkoutItems = (CheckoutItems) c2.uniqueResult();

                                    if (checkoutItems != null) {
                                        canContinue = true;
                                        responseObject.addProperty("contentCheckoutStatus", true);
                                        responseObject.addProperty("contentRatingStatus", true);
                                        responseObject.addProperty("contentRatingType", checkoutItems.getRatingType().getId());
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (canContinue) {
                        mainMovie.setUser(null);
                        responseObject.add("mainContent", gson.toJsonTree(mainMovie));

                        Criteria c1 = session.createCriteria(MainMovieHasGenre.class);
                        c1.add(Restrictions.eq("mainMovie", mainMovie));
                        List<MainMovieHasGenre> genreList = c1.list();
                        String genre_line = "";

                        for (MainMovieHasGenre mainMovieHasGenre : genreList) {
                            genre_line += mainMovieHasGenre.getGenre().getName() + ", ";
                        }
                        genre_line = genre_line.replaceAll(",\\s*$", "");
                        responseObject.addProperty("genre_line", genre_line);

                        Criteria c2 = session.createCriteria(MainMovieHasCountry.class);
                        c2.add(Restrictions.eq("mainMovie", mainMovie));
                        List<MainMovieHasCountry> countryList = c2.list();
                        String country_line = "";

                        for (MainMovieHasCountry mainMovieHasCountry : countryList) {
                            country_line += mainMovieHasCountry.getCountry().getName() + ", ";
                        }
                        country_line = country_line.replaceAll(",\\s*$", "");
                        responseObject.addProperty("country_line", country_line);

                        Criteria c3 = session.createCriteria(MainMovieHasLanguage.class);
                        c3.add(Restrictions.eq("mainMovie", mainMovie));
                        List<MainMovieHasLanguage> languageList = c3.list();
                        String language_line = "";

                        for (MainMovieHasLanguage mainMovieHasLanguage : languageList) {
                            language_line += mainMovieHasLanguage.getLanguage().getName() + ", ";
                        }
                        language_line = language_line.replaceAll(",\\s*$", "");
                        responseObject.addProperty("language_line", language_line);

                        ArrayList<Integer> ratingTypeIdList = new ArrayList<>();
                        ratingTypeIdList.add(1);
                        ratingTypeIdList.add(2);

                        Criteria c4 = session.createCriteria(RatingType.class);
                        c4.add(Restrictions.in("id", ratingTypeIdList));
                        List<RatingType> ratingTypeList = c4.list();

                        Criteria c5 = session.createCriteria(CheckoutItems.class);
                        c5.add(Restrictions.eq("mainMovie", mainMovie));
                        c5.add(Restrictions.in("ratingType", ratingTypeList));
                        responseObject.addProperty("ratingCount", c5.list().size());

                        responseObject.addProperty("watchlistStatus", false);

                        ArrayList<Watchlist> allWatchlistData = new ArrayList<Watchlist>();

                        if (ses != null && ses.getAttribute("user") != null) {
                            User user = (User) ses.getAttribute("user");

                            Criteria c6 = session.createCriteria(Watchlist.class);
                            c6.add(Restrictions.eq("user", user));
                            c6.add(Restrictions.eq("mainMovie", mainMovie));
                            Watchlist watchlist = (Watchlist) c6.uniqueResult();

                            if (watchlist != null) {
                                responseObject.addProperty("watchlistStatus", true);
                            }

                            Criteria c7 = session.createCriteria(Watchlist.class);
                            c7.add(Restrictions.eq("user", user));
                            allWatchlistData = (ArrayList<Watchlist>) c7.list();

                        } else {

                            if (ses != null && ses.getAttribute("sessionWatchlist") != null) {
                                allWatchlistData = (ArrayList<Watchlist>) ses.getAttribute("sessionWatchlist");

                                for (Watchlist watchlist : allWatchlistData) {
                                    if (watchlist.getMainMovie().getId() == mainMovie.getId()) {
                                        responseObject.addProperty("watchlistStatus", true);
                                    }
                                }
                            }
                        }
                        responseObject.add("allWatchlistData", gson.toJsonTree(allWatchlistData));

                        List<String> wordList = new ArrayList<>(Arrays.asList(mainMovie.getName().split("\\s+")));
                        Status status = (Status) session.get(Status.class, LoadSingleProduct.ACTIVE_STATUS);
                        ArrayList<MainMovie> similarFinalList = new ArrayList<MainMovie>();

                        for (String word : wordList) {
                            Criteria c6 = session.createCriteria(MainMovie.class);
                            c6.add(Restrictions.ne("id", mainMovie.getId()));
                            c6.add(Restrictions.eq("status", status));
                            c6.add(Restrictions.like("name", "%" + word + "%"));
                            List<MainMovie> similarContentList = c6.list();

                            for (MainMovie content : similarContentList) {
                                boolean alreadyExist = false;
                                for (MainMovie finalContent : similarFinalList) {
                                    if (finalContent.getId() == content.getId()) {
                                        alreadyExist = true;
                                    }
                                }
                                if (!alreadyExist && similarFinalList.size() < 12) {
                                    similarFinalList.add(content);
                                }
                            }
                        }

                        responseObject.add("similarFinalList", gson.toJsonTree(similarFinalList));
                        responseObject.addProperty("status", true);
                    } else {
                        responseObject.addProperty("message", "Content not found!");
                    }
                }

            } catch (Exception e) {
                responseObject.addProperty("message", "Content not found!");
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
