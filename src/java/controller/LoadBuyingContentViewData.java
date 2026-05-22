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
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadBuyingContentViewData", urlPatterns = {"/LoadBuyingContentViewData"})
public class LoadBuyingContentViewData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String contentId = request.getParameter("contentId");
        if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

            HttpSession ses = request.getSession(false);

            if (ses != null && ses.getAttribute("user") != null) {
                User user = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();
                Transaction tr = session.beginTransaction();

                try {

                    User verifiedUser = (User) session.get(User.class, user.getId());
                    if (verifiedUser != null) {

                        MainMovie mainContent = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
                        if (mainContent != null) {

                            Criteria c3 = session.createCriteria(Episodes.class);
                            c3.add(Restrictions.eq("mainMovie", mainContent));
                            c3.addOrder(Order.asc("registered_at"));
                            List<Episodes> episodesList = c3.list();

                            responseObject.addProperty("episodeCount", c3.list().size());
                            responseObject.add("mainContent", gson.toJsonTree(mainContent));
                            responseObject.add("episodesList", gson.toJsonTree(episodesList));
                            responseObject.addProperty("status", true);

                        }
                    }

                } catch (Exception e) {
                    tr.rollback();
                } finally {
                    session.close();
                }
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
