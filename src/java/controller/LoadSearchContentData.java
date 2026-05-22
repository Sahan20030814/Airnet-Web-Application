/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Country;
import hibernate.Genre;
import hibernate.HibernateUtil;
import hibernate.Language;
import hibernate.MainMovie;
import hibernate.MainMovieHasCountry;
import hibernate.MainMovieHasGenre;
import hibernate.MainMovieHasLanguage;
import hibernate.MovieType;
import hibernate.QualityType;
import hibernate.Status;
import hibernate.User;
import hibernate.Watchlist;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import static model.Util.getFirstDateOfYear;
import static model.Util.getLastDateOfYear;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadSearchContentData", urlPatterns = {"/LoadSearchContentData"})
public class LoadSearchContentData extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;
    private static final int MOVIE_TYPE_ID = 1;
    private static final int TV_SHOW_TYPE_ID = 2;
    private static final int MAX_RESULT = 16;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        responseObject.addProperty("userStatus", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            String firstResult = request.getParameter("firstResult");

            if (firstResult == null || !Util.isInteger(firstResult)) {
                firstResult = "0";
            }

            Criteria c1 = session.createCriteria(Genre.class);
            List<Genre> genreList = c1.list();
            responseObject.add("genreList", gson.toJsonTree(genreList));

            Criteria c2 = session.createCriteria(Country.class);
            List<Country> countryList = c2.list();
            responseObject.add("countryList", gson.toJsonTree(countryList));

            Criteria c3 = session.createCriteria(MovieType.class);
            List<MovieType> movieTypeList = c3.list();
            responseObject.add("movieTypeList", gson.toJsonTree(movieTypeList));

            Criteria c4 = session.createCriteria(QualityType.class);
            List<QualityType> qualityList = c4.list();
            responseObject.add("qualityList", gson.toJsonTree(qualityList));

            ArrayList<String> yearList = new ArrayList();
            for (int i = 0; i < 5; i++) {
                yearList.add(String.valueOf(Year.now().getValue() - i));
            }
            yearList.add("Older");
            responseObject.add("yearList", gson.toJsonTree(yearList));

            Criteria c5 = session.createCriteria(Language.class);
            List<Language> languageList = c5.list();
            responseObject.add("languageList", gson.toJsonTree(languageList));

            ArrayList<MainMovie> finalMovieList = new ArrayList<MainMovie>();
            String movieTypeId = request.getParameter("contentType");

            if (movieTypeId != null) {

                // get movies/tv shows related to selected genres
                String[] genreIds = request.getParameterValues("genreId");

                ArrayList<Genre> genreSearchList = new ArrayList();

                if (genreIds != null) {
                    for (String genreId : genreIds) {
                        if (Util.isInteger(genreId)) {
                            Genre genre = (Genre) session.get(Genre.class, Integer.parseInt(genreId));
                            if (genre != null) {
                                genreSearchList.add(genre);
                            }
                        }
                    }
                }

                ArrayList<MainMovie> mainMovieList = new ArrayList<MainMovie>();

                if (!genreSearchList.isEmpty()) {

                    Criteria c6 = session.createCriteria(MainMovieHasGenre.class);
                    c6.add(Restrictions.in("genre", genreSearchList));
                    List<MainMovieHasGenre> movieHasGenreList = c6.list();

                    for (MainMovieHasGenre movieHasGenre : movieHasGenreList) {
                        boolean alreadyExist = false;
                        for (MainMovie mainMovie : mainMovieList) {
                            if (mainMovie.getId() == movieHasGenre.getMainMovie().getId()) {
                                alreadyExist = true;
                                break;
                            }
                        }
                        if (!alreadyExist) {
                            mainMovieList.add(movieHasGenre.getMainMovie());
                        }
                    }
                }

                if (!(mainMovieList.isEmpty() && !genreSearchList.isEmpty()) || genreSearchList.isEmpty()) {

                    // get movies/tv shows related to selected country
                    String[] countryIds = request.getParameterValues("countryId");

                    ArrayList<Country> countrySearchList = new ArrayList();

                    if (countryIds != null) {
                        for (String countryId : countryIds) {
                            if (Util.isInteger(countryId)) {
                                Country country = (Country) session.get(Country.class, Integer.parseInt(countryId));
                                if (country != null) {
                                    countrySearchList.add(country);
                                }
                            }
                        }
                    }

                    if (!countrySearchList.isEmpty()) {
                        Criteria c6 = session.createCriteria(MainMovieHasCountry.class);

                        if ((!mainMovieList.isEmpty() && !genreSearchList.isEmpty())) {
                            c6.add(Restrictions.in("mainMovie", mainMovieList));
                        }

                        c6.add(Restrictions.in("country", countrySearchList));
                        List<MainMovieHasCountry> movieHasCountryList = c6.list();

                        mainMovieList = new ArrayList();

                        for (MainMovieHasCountry movieHasCountry : movieHasCountryList) {
                            boolean alreadyExist = false;
                            for (MainMovie mainMovie : mainMovieList) {
                                if (mainMovie.getId() == movieHasCountry.getMainMovie().getId()) {
                                    alreadyExist = true;
                                    break;
                                }
                            }
                            if (!alreadyExist) {
                                mainMovieList.add(movieHasCountry.getMainMovie());
                            }
                        }
                    }

                    if (!(mainMovieList.isEmpty() && !countrySearchList.isEmpty()) || (genreSearchList.isEmpty() && countrySearchList.isEmpty())) {

                        // get movies/tv shows related to selected language
                        String[] languageIds = request.getParameterValues("languageId");

                        ArrayList<Language> languageSearchList = new ArrayList();

                        if (languageIds != null) {
                            for (String languageId : languageIds) {
                                if (Util.isInteger(languageId)) {
                                    Language language = (Language) session.get(Language.class, Integer.parseInt(languageId));
                                    if (language != null) {
                                        languageSearchList.add(language);
                                    }
                                }
                            }
                        }

                        if (!languageSearchList.isEmpty()) {
                            Criteria c6 = session.createCriteria(MainMovieHasLanguage.class);

                            if ((!mainMovieList.isEmpty() && !countrySearchList.isEmpty())) {
                                c6.add(Restrictions.in("mainMovie", mainMovieList));
                            }

                            c6.add(Restrictions.in("language", languageSearchList));
                            List<MainMovieHasLanguage> movieHasLanguageList = c6.list();

                            mainMovieList = new ArrayList();

                            for (MainMovieHasLanguage movieHasLanguage : movieHasLanguageList) {
                                boolean alreadyExist = false;
                                for (MainMovie mainMovie : mainMovieList) {
                                    if (mainMovie.getId() == movieHasLanguage.getMainMovie().getId()) {
                                        alreadyExist = true;
                                        break;
                                    }
                                }
                                if (!alreadyExist) {
                                    mainMovieList.add(movieHasLanguage.getMainMovie());
                                }
                            }
                        }

                        if (genreSearchList.isEmpty() && countrySearchList.isEmpty() && languageSearchList.isEmpty()) {
                            Criteria c6 = session.createCriteria(MainMovie.class);
                            mainMovieList = (ArrayList) c6.list();
                        }

                        Criteria c6 = session.createCriteria(MainMovie.class);
                        Status status = (Status) session.get(Status.class, LoadSearchContentData.ACTIVE_STATUS);
                        c6.add(Restrictions.eq("status", status));

                        if (movieTypeId != null && !movieTypeId.equals("0")) {
                            if (Util.isInteger(movieTypeId)) {
                                MovieType movieType = (MovieType) session.get(MovieType.class, Integer.parseInt(movieTypeId));
                                if (movieType != null) {
                                    c6.add(Restrictions.eq("movieType", movieType));
                                }
                            }
                        }

                        String qualityTypeId = request.getParameter("quality");

                        if (qualityTypeId != null && !qualityTypeId.equals("0")) {
                            if (Util.isInteger(qualityTypeId)) {
                                QualityType qualityType = (QualityType) session.get(QualityType.class, Integer.parseInt(qualityTypeId));
                                if (qualityType != null) {
                                    c6.add(Restrictions.eq("qualityType", qualityType));
                                }
                            }
                        }

                        String releasedYear = request.getParameter("releasedYear");

                        if (releasedYear != null && !releasedYear.equals("0")) {

                            if (!releasedYear.equals("Older")) {
                                if (Util.isValidYear(releasedYear)) {
                                    //                                   c6.add(Restrictions.between("released_at",getFirstDateOfYear(releasedYear), getLastDateOfYear(releasedYear)));
                                    c6.add(Restrictions.ge("released_at", getFirstDateOfYear(releasedYear)));
                                    c6.add(Restrictions.le("released_at", getLastDateOfYear(releasedYear)));

                                }
                            } else {
                                Date olderDate = getLastDateOfYear(String.valueOf((Year.now().getValue()) - 5));
                                c6.add(Restrictions.le("released_at", olderDate));
                            }
                        }

                        String minPrice = request.getParameter("minPrice");
                        String maxPrice = request.getParameter("maxPrice");

                        if (minPrice != null && maxPrice != null) {
                            if (Util.isDouble(minPrice) && Util.isDouble(maxPrice)) {
                                c6.add(Restrictions.between("price", Double.parseDouble(minPrice), Double.parseDouble(maxPrice)));
                            } else if (Util.isDouble(minPrice) && !Util.isDouble(maxPrice)) {
                                c6.add(Restrictions.ge("price", Double.parseDouble(minPrice)));
                            } else if (!Util.isDouble(minPrice) && Util.isDouble(maxPrice)) {
                                c6.add(Restrictions.le("price", Double.parseDouble(maxPrice)));
                            }
                        }

                        List<MainMovie> wantedMovieList = c6.list();

                        ArrayList<MainMovie> temporyMovieList = new ArrayList<MainMovie>();
                        for (MainMovie wantedMovie : wantedMovieList) {
                            for (MainMovie mainMovie : mainMovieList) {
                                if (wantedMovie.getId() == mainMovie.getId()) {
                                    temporyMovieList.add(wantedMovie);
                                }
                            }
                        }
                        responseObject.addProperty("allContentCount", temporyMovieList.size());

                        int count = 1;

                        for (MainMovie wantedMovie : wantedMovieList) {
                            for (MainMovie mainMovie : mainMovieList) {
                                if (wantedMovie.getId() == mainMovie.getId() && finalMovieList.size() < MAX_RESULT) {
                                    if (count > Integer.parseInt(firstResult)) {
                                        finalMovieList.add(wantedMovie);
                                    }
                                    count++;
                                }
                            }
                        }

                    }
                }

                responseObject.addProperty("searchedTitle", "Filter results");

            } else {
                String movieTitle = request.getParameter("name");

                if (movieTitle != null) {
                    Criteria c6 = session.createCriteria(MainMovie.class);
                    Status status = (Status) session.get(Status.class, LoadSearchContentData.ACTIVE_STATUS);
                    c6.add(Restrictions.eq("status", status));
                    c6.add(Restrictions.like("name", "%" + movieTitle + "%"));
                    responseObject.addProperty("allContentCount", c6.list().size());

                    c6.setFirstResult(Integer.parseInt(firstResult));
                    c6.setMaxResults(MAX_RESULT);
                    finalMovieList = (ArrayList<MainMovie>) c6.list();

                    responseObject.addProperty("searchedTitle", "Search results for \"" + movieTitle + "\"");

                } else {

                    String genreId = request.getParameter("genreId");

                    if (genreId != null) {
                        if (Util.isInteger(genreId)) {

                            Genre genre = (Genre) session.get(Genre.class, Integer.parseInt(genreId));
                            if (genre != null) {
                                Criteria c6 = session.createCriteria(MainMovieHasGenre.class);
                                c6.add(Restrictions.eq("genre", genre));
                                responseObject.addProperty("allContentCount", c6.list().size());

                                c6.setFirstResult(Integer.parseInt(firstResult));
                                c6.setMaxResults(MAX_RESULT);

                                List<MainMovieHasGenre> movieHasGenreList = c6.list();
                                for (MainMovieHasGenre mainMovieHasGenre : movieHasGenreList) {
                                    finalMovieList.add(mainMovieHasGenre.getMainMovie());
                                }
                                responseObject.addProperty("searchedTitle", genre.getName() + " Movies & TV Shows");
                            }
                        }

                    } else {
                        String countryId = request.getParameter("countryId");

                        if (countryId != null) {
                            if (Util.isInteger(countryId)) {

                                Country country = (Country) session.get(Country.class, Integer.parseInt(countryId));
                                if (country != null) {
                                    Criteria c6 = session.createCriteria(MainMovieHasCountry.class);
                                    c6.add(Restrictions.eq("country", country));
                                    responseObject.addProperty("allContentCount", c6.list().size());

                                    c6.setFirstResult(Integer.parseInt(firstResult));
                                    c6.setMaxResults(MAX_RESULT);

                                    List<MainMovieHasCountry> movieHasCountryList = c6.list();
                                    for (MainMovieHasCountry mainMovieHasCountry : movieHasCountryList) {
                                        finalMovieList.add(mainMovieHasCountry.getMainMovie());
                                    }
                                    responseObject.addProperty("searchedTitle", country.getName());
                                }
                            }

                        } else {

                            String content = request.getParameter("content");
                            Criteria c6 = session.createCriteria(MainMovie.class);
                            Status status = (Status) session.get(Status.class, LoadSearchContentData.ACTIVE_STATUS);

                            if (content != null) {
                                if (content.equals("topRatingMoviesTvShows")) {
                                    //top rating movies/Tv Shows
                                    c6.add(Restrictions.eq("status", status));
                                    c6.addOrder(Order.desc("rating"));
                                    responseObject.addProperty("allContentCount", c6.list().size());

                                    c6.setFirstResult(Integer.parseInt(firstResult));
                                    c6.setMaxResults(MAX_RESULT);

                                    finalMovieList = (ArrayList<MainMovie>) c6.list();
                                    responseObject.addProperty("searchedTitle", "Top IMDB Rating");
                                } else if (content.equals("latestMovies")) {
                                    //latest movies
                                    c6.add(Restrictions.eq("status", status));
                                    MovieType movieType = (MovieType) session.get(MovieType.class, LoadSearchContentData.MOVIE_TYPE_ID);
                                    c6.add(Restrictions.eq("movieType", movieType));
                                    c6.addOrder(Order.desc("released_at"));
                                    responseObject.addProperty("allContentCount", c6.list().size());

                                    c6.setFirstResult(Integer.parseInt(firstResult));
                                    c6.setMaxResults(MAX_RESULT);

                                    finalMovieList = (ArrayList<MainMovie>) c6.list();
                                    responseObject.addProperty("searchedTitle", "Latest Movies");
                                } else if (content.equals("latestTvShows")) {
                                    //latest tv shows
                                    c6.add(Restrictions.eq("status", status));
                                    MovieType tvShowType = (MovieType) session.get(MovieType.class, LoadSearchContentData.TV_SHOW_TYPE_ID);
                                    c6.add(Restrictions.eq("movieType", tvShowType));
                                    c6.addOrder(Order.desc("released_at"));
                                    responseObject.addProperty("allContentCount", c6.list().size());

                                    c6.setFirstResult(Integer.parseInt(firstResult));
                                    c6.setMaxResults(MAX_RESULT);

                                    finalMovieList = (ArrayList<MainMovie>) c6.list();
                                    responseObject.addProperty("searchedTitle", "Latest TV Shows");
                                } else if (content.equals("trendingMoviesTvShows")) {
                                    //trending movies
                                    c6.add(Restrictions.eq("status", status));
                                    c6.addOrder(Order.desc("selling_count"));
                                    responseObject.addProperty("allContentCount", c6.list().size());

                                    c6.setFirstResult(Integer.parseInt(firstResult));
                                    c6.setMaxResults(MAX_RESULT);

                                    finalMovieList = (ArrayList<MainMovie>) c6.list();
                                    responseObject.addProperty("searchedTitle", "Trending Movies & TV Shows");
                                }
                            } else {
                                //Latest Movies & Tv Shows
                                c6.add(Restrictions.eq("status", status));
                                c6.addOrder(Order.desc("released_at"));
                                responseObject.addProperty("allContentCount", c6.list().size());

                                c6.setFirstResult(Integer.parseInt(firstResult));
                                c6.setMaxResults(MAX_RESULT);

                                finalMovieList = (ArrayList<MainMovie>) c6.list();
                                responseObject.addProperty("searchedTitle", "Movies & Tv Shows");
                            }
                        }
                    }
                }
            }

            ArrayList<Watchlist> watchListItemList = new ArrayList<Watchlist>();

            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                Criteria c6 = session.createCriteria(Watchlist.class);
                c6.add(Restrictions.eq("user", user));
                watchListItemList = (ArrayList<Watchlist>) c6.list();
            } else {
                if (request.getSession() != null && request.getSession().getAttribute("sessionWatchlist") != null) {
                    watchListItemList = (ArrayList<Watchlist>) request.getSession().getAttribute("sessionWatchlist");
                }
            }
            responseObject.add("watchListItemList", gson.toJsonTree(watchListItemList));

            for (MainMovie mainMovie : finalMovieList) {
                mainMovie.setUser(null);
            }

            responseObject.add("finalMovieList", gson.toJsonTree(finalMovieList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("status", false);
        }
        session.close();

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
