/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import hibernate.HibernateUtil;
import hibernate.MainMovie;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "CheckSessionWatchlist", urlPatterns = {"/CheckSessionWatchlist"})
public class CheckSessionWatchlist extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyUser = (User) session.get(User.class, user.getId());
            if (verifyUser != null) {

                ArrayList<Watchlist> sessionWatchlists = (ArrayList<Watchlist>) request.getSession().getAttribute("sessionWatchlist");
                if (sessionWatchlists != null) {

                    for (Watchlist sessionWatchlist : sessionWatchlists) {
                        MainMovie content = (MainMovie) session.get(MainMovie.class, sessionWatchlist.getMainMovie().getId());

                        if (content != null) {
                            Criteria c1 = session.createCriteria(Watchlist.class);
                            c1.add(Restrictions.eq("user", verifyUser));
                            c1.add(Restrictions.eq("mainMovie", content));
                            List<Watchlist> watchlistDataList = c1.list();

                            if (watchlistDataList == null || watchlistDataList.isEmpty()) {
                                sessionWatchlist.setUser(verifyUser);
                                session.save(sessionWatchlist);
                                session.beginTransaction().commit();
                            }
                        }
                    }
                    request.getSession().setAttribute("sessionWatchlist", null);
                }
            }
            session.close();
        }
    }
}
