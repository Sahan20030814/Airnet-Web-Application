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
@WebServlet(name = "LoadOwnMovies", urlPatterns = {"/LoadOwnMovies"})
public class LoadOwnMovies extends HttpServlet {

    private static final int MOVIE_TYPE_ID = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String movieName = request.getParameter("movieName");

        if (request.getSession() != null && request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyedUser = (User) session.get(User.class, user.getId());

            if (verifyedUser != null) {
                MovieType movieType = (MovieType) session.get(MovieType.class, LoadOwnMovies.MOVIE_TYPE_ID);

                if (movieType != null) {
                    Criteria c1 = session.createCriteria(MainMovie.class);
                    c1.add(Restrictions.eq("movieType", movieType));
                    c1.add(Restrictions.eq("user", verifyedUser));
                    c1.add(Restrictions.like("name", movieName + "%"));
                    c1.addOrder(Order.desc("registered_at"));
                    List<MainMovie> ownMovieList = c1.list();
                    responseObject.addProperty("movieCount", c1.list().size());
                    responseObject.add("ownMovieList", gson.toJsonTree(ownMovieList));

                    Criteria c2 = session.createCriteria(MainMovieHasGenre.class);
                    List<MainMovieHasGenre> movieHasGenreList = c2.list();
                    responseObject.add("movieHasGenreList", gson.toJsonTree(movieHasGenreList));

                    Criteria c3 = session.createCriteria(MainMovieHasCountry.class);
                    List<MainMovieHasCountry> movieHasCountryList = c3.list();
                    responseObject.add("movieHasCountryList", gson.toJsonTree(movieHasCountryList));

                    Criteria c4 = session.createCriteria(MainMovieHasLanguage.class);
                    List<MainMovieHasLanguage> movieHasLanguageList = c4.list();
                    responseObject.add("movieHasLanguageList", gson.toJsonTree(movieHasLanguageList));

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
