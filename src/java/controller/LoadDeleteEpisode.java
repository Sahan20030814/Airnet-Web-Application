/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Episodes;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
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
@WebServlet(name = "LoadDeleteEpisode", urlPatterns = {"/LoadDeleteEpisode"})
public class LoadDeleteEpisode extends HttpServlet {

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
                String contentId = request.getParameter("contentId");

                if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

                    MainMovie mainMovie = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));

                    if (mainMovie != null) {

                        Criteria c2 = session.createCriteria(Episodes.class);
                        c2.add(Restrictions.eq("mainMovie", mainMovie));
                        List<Episodes> contentEpisodeList = c2.list();

                        responseObject.add("contentEpisodeList", gson.toJsonTree(contentEpisodeList));
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
