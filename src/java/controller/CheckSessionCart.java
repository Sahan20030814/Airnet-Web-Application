/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import hibernate.Cart;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "CheckSessionCart", urlPatterns = {"/CheckSessionCart"})
public class CheckSessionCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyUser = (User) session.get(User.class, user.getId());
            if (verifyUser != null) {

                ArrayList<Cart> sessionCarts = (ArrayList<Cart>) request.getSession().getAttribute("sessionCart");
                if (sessionCarts != null && sessionCarts.size() > 0) {

                    Criteria c2 = session.createCriteria(Checkout.class);
                    c2.add(Restrictions.eq("user", verifyUser));
                    List<Checkout> checkoutList = c2.list();

                    for (Cart sessionCart : sessionCarts) {
                        MainMovie content = (MainMovie) session.get(MainMovie.class, sessionCart.getMainMovie().getId());

                        if (content != null) {
                            if (content.getUser().getId() != verifyUser.getId()) {

                                Criteria c1 = session.createCriteria(Cart.class);
                                c1.add(Restrictions.eq("user", verifyUser));
                                c1.add(Restrictions.eq("mainMovie", content));
                                List<Cart> cartList = c1.list();

                                List<CheckoutItems> checkoutItemsList = null;

                                if (cartList == null || cartList.size() == 0) {

                                    if (!checkoutList.isEmpty()) {
                                        Criteria c3 = session.createCriteria(CheckoutItems.class);
                                        c3.add(Restrictions.eq("mainMovie", content));
                                        c3.add(Restrictions.in("checkout", checkoutList));
                                        checkoutItemsList = c3.list();
                                    }

                                    if (checkoutItemsList == null || checkoutItemsList.size() == 0) {
                                        sessionCart.setUser(verifyUser);
                                        session.save(sessionCart);
                                        session.beginTransaction().commit();
                                    }
                                }
                            }
                        }
                    }
                    request.getSession().setAttribute("sessionCart", null);
                }
            }
            session.close();
        }
    }
}
