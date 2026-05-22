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
import hibernate.MainMovie;
import hibernate.MovieType;
import hibernate.Status;
import hibernate.User;
import hibernate.Watchlist;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadIndexData", urlPatterns = {"/LoadIndexData"})
public class LoadIndexData extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;
    private static final int MOVIE_TYPE_ID = 1;
    private static final int TV_SHOW_TYPE_ID = 2;
    private static final int MAX_RESULT = 12;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        try {
            Criteria c1 = session.createCriteria(Genre.class);
            List<Genre> genreList = c1.list();
            responseObject.add("genreList", gson.toJsonTree(genreList));

            Criteria c2 = session.createCriteria(Country.class);
            List<Country> countryList = c2.list();
            responseObject.add("countryList", gson.toJsonTree(countryList));

            Status status = (Status) session.get(Status.class, LoadIndexData.ACTIVE_STATUS);
            MovieType movieType = (MovieType) session.get(MovieType.class, LoadIndexData.MOVIE_TYPE_ID);

            //trending movies
            Criteria c3 = session.createCriteria(MainMovie.class);
            c3.add(Restrictions.eq("status", status));
            c3.add(Restrictions.eq("movieType", movieType));
            c3.addOrder(Order.desc("selling_count"));
            c3.setMaxResults(LoadIndexData.MAX_RESULT);
            List<MainMovie> trendingMovieList = c3.list();

            for (MainMovie mainMovie : trendingMovieList) {
                mainMovie.setUser(null);
            }
            responseObject.add("trendingMovieList", gson.toJsonTree(trendingMovieList));

            MovieType tvShowType = (MovieType) session.get(MovieType.class, LoadIndexData.TV_SHOW_TYPE_ID);

            //trending tv shows
            Criteria c4 = session.createCriteria(MainMovie.class);
            c4.add(Restrictions.eq("status", status));
            c4.add(Restrictions.eq("movieType", tvShowType));
            c4.addOrder(Order.desc("selling_count"));
            c4.setMaxResults(LoadIndexData.MAX_RESULT);
            List<MainMovie> trendingTvShowList = c4.list();

            for (MainMovie mainMovie : trendingTvShowList) {
                mainMovie.setUser(null);
            }
            responseObject.add("trendingTvShowList", gson.toJsonTree(trendingTvShowList));

            //latest movies
            Criteria c5 = session.createCriteria(MainMovie.class);
            c5.add(Restrictions.eq("status", status));
            c5.add(Restrictions.eq("movieType", movieType));
            c5.addOrder(Order.desc("released_at"));
            c5.setMaxResults(LoadIndexData.MAX_RESULT);
            List<MainMovie> latestMovieList = c5.list();

            for (MainMovie mainMovie : latestMovieList) {
                mainMovie.setUser(null);
            }
            responseObject.add("latestMovieList", gson.toJsonTree(latestMovieList));

            //latest tv shows
            Criteria c6 = session.createCriteria(MainMovie.class);
            c6.add(Restrictions.eq("status", status));
            c6.add(Restrictions.eq("movieType", tvShowType));
            c6.addOrder(Order.desc("released_at"));
            c6.setMaxResults(LoadIndexData.MAX_RESULT);
            List<MainMovie> latestTvShowList = c6.list();

            for (MainMovie mainMovie : latestTvShowList) {
                mainMovie.setUser(null);
            }
            responseObject.add("latestTvShowList", gson.toJsonTree(latestTvShowList));

            ArrayList<Watchlist> watchListItemList = new ArrayList<Watchlist>();

            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                Criteria c7 = session.createCriteria(Watchlist.class);
                c7.add(Restrictions.eq("user", user));
                watchListItemList = (ArrayList<Watchlist>) c7.list();
            } else {
                if (request.getSession() != null && request.getSession().getAttribute("sessionWatchlist") != null) {
                    watchListItemList = (ArrayList<Watchlist>) request.getSession().getAttribute("sessionWatchlist");
                }
            }
            responseObject.add("watchListItemList", gson.toJsonTree(watchListItemList));
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
