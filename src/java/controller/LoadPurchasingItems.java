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
import java.util.ArrayList;
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
@WebServlet(name = "LoadPurchasingItems", urlPatterns = {"/LoadPurchasingItems"})
public class LoadPurchasingItems extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String orderId = request.getParameter("orderId");
        String itemName = request.getParameter("itemName");

        if (request.getSession() != null && request.getSession().getAttribute("user") != null) {
            User user = (User) request.getSession().getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyedUser = (User) session.get(User.class, user.getId());

            if (verifyedUser != null) {

                Criteria c1 = session.createCriteria(Checkout.class);
                c1.add(Restrictions.eq("user", verifyedUser));
                List<Checkout> checkoutList = c1.list();

                Criteria c2 = session.createCriteria(MainMovie.class);
                c2.add(Restrictions.like("name", itemName + "%"));
                List<MainMovie> movieList = c2.list();

                List<CheckoutItems> checkoutItemList = null;

                if (checkoutList.size() > 0 && movieList.size() > 0) {
                    Criteria c3 = session.createCriteria(CheckoutItems.class);
                    c3.add(Restrictions.in("checkout", checkoutList));
                    c3.add(Restrictions.in("mainMovie", movieList));

                    if (orderId == null || !Util.isInteger(orderId)) {
                        c3.addOrder(Order.desc("registered_at"));
                    } else if (orderId.equals("1")) {
                        c3.addOrder(Order.asc("registered_at"));
                    } else if (orderId.equals("2")) {
                        c3.addOrder(Order.desc("registered_at"));
                    } else {
                        c3.addOrder(Order.desc("registered_at"));
                    }

                    checkoutItemList = c3.list();
                }

                if (checkoutItemList == null) {
                    checkoutItemList = new ArrayList();
                }

                responseObject.addProperty("itemCount", checkoutItemList.size());
                responseObject.add("checkoutItemList", gson.toJsonTree(checkoutItemList));
                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
