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
import hibernate.User;
import java.io.IOException;
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
@WebServlet(name = "LoadOwnTvShows", urlPatterns = {"/LoadOwnTvShows"})
public class LoadOwnTvShows extends HttpServlet {

    private static final int TV_SHOW_TYPE_ID = 2;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String tvShowName = request.getParameter("tvShowName");

        if (request.getSession() != null && request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyedUser = (User) session.get(User.class, user.getId());

            if (verifyedUser != null) {
                MovieType movieType = (MovieType) session.get(MovieType.class, LoadOwnTvShows.TV_SHOW_TYPE_ID);

                if (movieType != null) {
                    Criteria c1 = session.createCriteria(MainMovie.class);
                    c1.add(Restrictions.eq("movieType", movieType));
                    c1.add(Restrictions.eq("user", verifyedUser));
                    c1.add(Restrictions.like("name", tvShowName + "%"));
                    c1.addOrder(Order.desc("registered_at"));
                    List<MainMovie> ownTvShowList = c1.list();
                    responseObject.addProperty("tvShowCount", c1.list().size());
                    responseObject.add("ownTvShowList", gson.toJsonTree(ownTvShowList));

                    Criteria c2 = session.createCriteria(MainMovieHasGenre.class);
                    List<MainMovieHasGenre> tvShowHasGenreList = c2.list();
                    responseObject.add("tvShowHasGenreList", gson.toJsonTree(tvShowHasGenreList));

                    Criteria c3 = session.createCriteria(MainMovieHasCountry.class);
                    List<MainMovieHasCountry> tvShowHasCountryList = c3.list();
                    responseObject.add("tvShowHasCountryList", gson.toJsonTree(tvShowHasCountryList));

                    Criteria c4 = session.createCriteria(MainMovieHasLanguage.class);
                    List<MainMovieHasLanguage> tvShowHasLanguageList = c4.list();
                    responseObject.add("tvShowHasLanguageList", gson.toJsonTree(tvShowHasLanguageList));

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
