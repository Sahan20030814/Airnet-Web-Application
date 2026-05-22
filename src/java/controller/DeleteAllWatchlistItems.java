/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "DeleteAllWatchlistItems", urlPatterns = {"/DeleteAllWatchlistItems"})
public class DeleteAllWatchlistItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();

        try {
            if (request.getSession() != null && request.getSession().getAttribute("user") != null) {
                User user = (User) request.getSession().getAttribute("user");

                User verifyedUser = (User) session.get(User.class, user.getId());
                if (verifyedUser != null) {
                    Criteria c1 = session.createCriteria(Watchlist.class);
                    c1.add(Restrictions.eq("user", verifyedUser));
                    List<Watchlist> watchlistDataList = c1.list();

                    if (watchlistDataList != null && !watchlistDataList.isEmpty()) {
                        for (Watchlist watchlist : watchlistDataList) {
                            Watchlist watchlistItem = (Watchlist) session.get(Watchlist.class, watchlist.getId());
                            if (watchlistItem != null) {
                                session.delete(watchlistItem);
                            }
                        }
                        session.flush();
                        session.clear();
                        tr.commit();
                    }
                }

                ArrayList<Watchlist> sessionWatchlists = (ArrayList<Watchlist>) request.getSession().getAttribute("sessionWatchlist");
                if (sessionWatchlists != null) {
                    request.getSession().setAttribute("sessionWatchlist", null);
                }
            }
        } catch (Exception e) {
            tr.rollback();
        } finally {
            session.close();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
