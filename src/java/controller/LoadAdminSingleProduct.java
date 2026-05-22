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
import java.io.IOException;
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
@WebServlet(name = "LoadAdminSingleProduct", urlPatterns = {"/LoadAdminSingleProduct"})
public class LoadAdminSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String contentId = request.getParameter("id");

        if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            try {
                MainMovie mainMovie = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
                if (mainMovie != null) {

                    HttpSession ses = request.getSession(false);
                    if (ses != null && ses.getAttribute("admin") != null) {

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

                        responseObject.addProperty("status", true);
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
