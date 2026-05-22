/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.Status;
import hibernate.User;
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
@WebServlet(name = "LoadCartItems", urlPatterns = {"/LoadCartItems"})
public class LoadCartItems extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        responseObject.addProperty("cartItemsCount", 0);

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {   // DB Cart
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Status status = (Status) session.get(Status.class, LoadCartItems.ACTIVE_STATUS);
            Criteria c1 = session.createCriteria(MainMovie.class);
            c1.add(Restrictions.eq("status", status));
            List<MainMovie> contentList = c1.list();

            User verifyedUser = (User) session.get(User.class, user.getId());

            if (verifyedUser != null) {
                Criteria c2 = session.createCriteria(Cart.class);
                c2.add(Restrictions.eq("user", verifyedUser));
                c2.add(Restrictions.in("mainMovie", contentList));
                List<Cart> cartItemList = c2.list();

                responseObject.addProperty("cartItemsCount", c2.list().size());

                for (Cart cart : cartItemList) {
                    cart.getMainMovie().setUser(null);
                    cart.setUser(null);
                }
                responseObject.addProperty("cartItemsCount", cartItemList.size());
                responseObject.add("cartItemsList", gson.toJsonTree(cartItemList));
                responseObject.addProperty("status", true);
            }
            session.close();
        } else {                // Session Cart
            ArrayList<Cart> sessionCarts = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");

            if (sessionCarts != null) {
                if (sessionCarts.isEmpty()) {
                    responseObject.addProperty("cartItemsCount", 0);
                } else {
                    for (Cart sessionCart : sessionCarts) {
                        sessionCart.getMainMovie().setUser(null);
                        sessionCart.setUser(null);
                        if (sessionCart.getMainMovie().getStatus().getId() != LoadCartItems.ACTIVE_STATUS) {
                            sessionCarts.remove(sessionCart);
                        }
                    }
                    responseObject.addProperty("cartItemsCount", sessionCarts.size());
                    responseObject.add("cartItemsList", gson.toJsonTree(sessionCarts));
                    responseObject.addProperty("status", true);
                }
            } else {
                responseObject.addProperty("cartItemsCount", 0);
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
