/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.MainMovieHasCountry;
import hibernate.MainMovieHasGenre;
import hibernate.MainMovieHasLanguage;
import hibernate.MovieType;
import hibernate.Status;
import hibernate.User;
import hibernate.Watchlist;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadWatchlistItems", urlPatterns = {"/LoadWatchlistItems"})
public class LoadWatchlistItems extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String itemName = request.getParameter("itemName");
        String typeId = request.getParameter("typeId");

        if (request.getSession() != null && request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifiedUser = (User) session.get(User.class, user.getId());

            if (verifiedUser != null) {
                MovieType movieType = null;

                if (typeId != null && !typeId.equals("0") && Util.isInteger(typeId)) {
                    movieType = (MovieType) session.get(MovieType.class, Integer.parseInt(typeId));
                }

                Criteria c1 = session.createCriteria(MainMovie.class);
                Status status = (Status) session.get(Status.class, LoadWatchlistItems.ACTIVE_STATUS);
                c1.add(Restrictions.eq("status", status));
                if (movieType != null) {
                    c1.add(Restrictions.eq("movieType", movieType));
                }
                c1.add(Restrictions.like("name", itemName + "%"));
                c1.addOrder(Order.desc("registered_at"));
                List<MainMovie> contentList = c1.list();

                if (contentList.size() > 0) {
                    Criteria c2 = session.createCriteria(Watchlist.class);
                    c2.add(Restrictions.eq("user", verifiedUser));
                    c2.add(Restrictions.in("mainMovie", contentList));
                    List<MainMovieHasGenre> watchlistItemList = c2.list();
                    responseObject.addProperty("watchlistItemCount", c2.list().size());
                    responseObject.add("watchlistItemList", gson.toJsonTree(watchlistItemList));

                    Criteria c3 = session.createCriteria(MainMovieHasGenre.class);
                    List<MainMovieHasGenre> contentHasGenreList = c3.list();
                    responseObject.add("contentHasGenreList", gson.toJsonTree(contentHasGenreList));

                    Criteria c4 = session.createCriteria(MainMovieHasCountry.class);
                    List<MainMovieHasCountry> contentHasCountryList = c4.list();
                    responseObject.add("contentHasCountryList", gson.toJsonTree(contentHasCountryList));

                    Criteria c5 = session.createCriteria(MainMovieHasLanguage.class);
                    List<MainMovieHasLanguage> contentHasLanguageList = c5.list();
                    responseObject.add("contentHasLanguageList", gson.toJsonTree(contentHasLanguageList));

                    responseObject.addProperty("status", true);
                }

            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
