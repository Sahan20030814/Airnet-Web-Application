/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.Status;
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
@WebServlet(name = "LoadCarouselData", urlPatterns = {"/LoadCarouselData"})
public class LoadCarouselData extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;
    private static final int MAX_RESULT = 8;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = null;

        try {
            session = sf.openSession();

            Status status = (Status) session.get(Status.class, LoadCarouselData.ACTIVE_STATUS);

            //trending movies
            Criteria c1 = session.createCriteria(MainMovie.class);
            c1.add(Restrictions.eq("status", status));
            c1.addOrder(Order.desc("selling_count"));
            c1.addOrder(Order.desc("released_at"));
            c1.setMaxResults(LoadCarouselData.MAX_RESULT);
            List<MainMovie> trendingMovieList = c1.list();

            for (MainMovie mainMovie : trendingMovieList) {
                mainMovie.setUser(null);
            }
            responseObject.add("trendingList", gson.toJsonTree(trendingMovieList));
            responseObject.addProperty("status", true);

        } catch (Exception e) {
            responseObject.addProperty("status", false);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
