/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Checkout;
import hibernate.CheckoutItems;
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
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadContentBuyingUsers", urlPatterns = {"/LoadContentBuyingUsers"})
public class LoadContentBuyingUsers extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        responseObject.addProperty("contentTitle", "Invalid Content");
        Gson gson = new Gson();

        String contentId = request.getParameter("contentId");
        String invoiceId = request.getParameter("checkoutId");

        if (invoiceId != null && invoiceId.contains("#")) {
            invoiceId = invoiceId.replace("#", "");
        }

        if (request.getSession() != null && request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyedUser = (User) session.get(User.class, user.getId());
            if (verifyedUser != null) {

                List<Checkout> checkoutList = null;

                Criteria c1 = session.createCriteria(Checkout.class);
                if (invoiceId == null || invoiceId.isEmpty()) {
                    checkoutList = c1.list();
                } else if (Util.isInteger(invoiceId)) {
                    c1.add(Restrictions.eq("id", Integer.parseInt(invoiceId)));
                    checkoutList = c1.list();
                }

                if (checkoutList != null) {
                    if (checkoutList.size() > 0 && contentId != null && Util.isInteger(contentId)) {

                        Criteria c2 = session.createCriteria(MainMovie.class);
                        c2.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                        c2.add(Restrictions.eq("user", verifyedUser));
                        MainMovie mainContent = (MainMovie) c2.uniqueResult();

                        if (mainContent != null) {
                            responseObject.addProperty("contentTitle", "Users List of \"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName());

                            Criteria c3 = session.createCriteria(CheckoutItems.class);
                            c3.add(Restrictions.eq("mainMovie", mainContent));
                            c3.add(Restrictions.in("checkout", checkoutList));
                            c3.addOrder(Order.desc("registered_at"));
                            List<CheckoutItems> buyingContentUsersList = c3.list();

                            responseObject.addProperty("buyingContentUsersCount", c3.list().size());
                            responseObject.add("buyingContentUsersList", gson.toJsonTree(buyingContentUsersList));
                            responseObject.addProperty("status", true);
                        }
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
