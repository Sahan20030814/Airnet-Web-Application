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
import hibernate.User;
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
@WebServlet(name = "LoadUpdatedContentData", urlPatterns = {"/LoadUpdatedContentData"})
public class LoadUpdatedContentData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {

            String contentId = request.getParameter("id");

            if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();

                User user = (User) ses.getAttribute("user");

                try {
                    Criteria c1 = session.createCriteria(MainMovie.class);
                    c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                    c1.add(Restrictions.eq("user", user));
                    MainMovie mainMovie = (MainMovie) c1.uniqueResult();

                    if (mainMovie != null) {
                        responseObject.add("mainContent", gson.toJsonTree(mainMovie));

                        Criteria c2 = session.createCriteria(MainMovieHasGenre.class);
                        c2.add(Restrictions.eq("mainMovie", mainMovie));
                        List<MainMovieHasGenre> genreList = c2.list();
                        responseObject.add("contentGenreList", gson.toJsonTree(genreList));

                        Criteria c3 = session.createCriteria(MainMovieHasCountry.class);
                        c3.add(Restrictions.eq("mainMovie", mainMovie));
                        List<MainMovieHasCountry> countryList = c3.list();
                        responseObject.add("contentCountryList", gson.toJsonTree(countryList));

                        Criteria c4 = session.createCriteria(MainMovieHasLanguage.class);
                        c4.add(Restrictions.eq("mainMovie", mainMovie));
                        List<MainMovieHasLanguage> languageList = c4.list();
                        responseObject.add("contentLanguageList", gson.toJsonTree(languageList));

                        responseObject.addProperty("status", true);
                    }

                } catch (Exception e) {
                    responseObject.addProperty("message", "Content not found!");
                }

                session.close();
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
