/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.MovieType;
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
@WebServlet(name = "LoadEpisodeContents", urlPatterns = {"/LoadEpisodeContents"})
public class LoadEpisodeContents extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        HttpSession ses = request.getSession(false);

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c1 = session.createCriteria(User.class);
            c1.add(Restrictions.eq("email", user.getEmail()));
            User u1 = (User) c1.uniqueResult();

            if (u1 != null) {
                String typeId = request.getParameter("typeId");

                if (typeId != null && !typeId.isEmpty() && Util.isInteger(typeId)) {

                    MovieType movieType = (MovieType) session.get(MovieType.class, Integer.parseInt(typeId));

                    if (movieType != null) {

                        Criteria c2 = session.createCriteria(MainMovie.class);
                        c2.add(Restrictions.eq("movieType", movieType));
                        c2.add(Restrictions.eq("user", user));
                        List<MainMovie> episodeContentList = c2.list();
                        responseObject.add("episodeContentList", gson.toJsonTree(episodeContentList));

                        responseObject.addProperty("status", true);
                    }

                }

            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
