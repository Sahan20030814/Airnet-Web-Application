/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.Checkout;
import hibernate.CheckoutItems;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.RatingType;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "CartCheckOutToDatabase", urlPatterns = {"/CartCheckOutToDatabase"})
public class CartCheckOutToDatabase extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;
    private static final int DEFAULT_RATING_TYPE_ID = 0;
    private static final double SITE_CONTENT_SELLING_RATE = 5;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        String contentIds = requJsonObject.get("content_ids").getAsString();
        String checkoutId = requJsonObject.get("checkoutId").getAsString();

        String[] ids = contentIds.split(",");

        ArrayList<String> cartContentIdList = new ArrayList<>();
        for (String id : ids) {
            cartContentIdList.add(id.trim()); // remove extra spaces
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {
            User user = (User) request.getSession().getAttribute("user");
            if (user != null) {
                User verifyUser = (User) session.get(User.class, user.getId());

                if (verifyUser != null) {
                    if (cartContentIdList.size() > 0) {
                        Status status = (Status) session.get(Status.class, CartCheckOutToDatabase.ACTIVE_STATUS);
                        ArrayList<Cart> cartList = new ArrayList();
                        boolean canContinue = false;

                        for (String contentId : cartContentIdList) {
                            if (Util.isInteger(contentId)) {
                                Criteria c1 = session.createCriteria(MainMovie.class);
                                c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                                c1.add(Restrictions.eq("status", status));
                                MainMovie checkoutContent = (MainMovie) c1.uniqueResult();

                                if (checkoutContent != null) {
                                    Criteria c2 = session.createCriteria(Cart.class);
                                    c2.add(Restrictions.eq("user", verifyUser));
                                    c2.add(Restrictions.eq("mainMovie", checkoutContent));
                                    Cart cart = (Cart) c2.uniqueResult();

                                    if (cart != null) {
                                        cartList.add(cart);
                                        canContinue = true;
                                    } else {
                                        canContinue = false;
                                    }
                                } else {
                                    canContinue = false;
                                }
                            } else {
                                canContinue = false;
                            }
                        }

                        if (canContinue && cartList.size() > 0 && Util.isInteger(checkoutId)) {
                            RatingType defaultRatingType = (RatingType) session.get(RatingType.class, CartCheckOutToDatabase.DEFAULT_RATING_TYPE_ID);

                            if (defaultRatingType != null) {
                                Date ontime = new Date();
                                Checkout checkout = new Checkout();
                                checkout.setId(Integer.parseInt(checkoutId));
                                checkout.setUser(user);
                                checkout.setRegistered_at(ontime);

                                session.save(checkout);

                                for (Cart cart : cartList) {
                                    Cart cartItem = (Cart) session.get(Cart.class, cart.getId());

                                    if (cartItem != null) {
                                        double site_price = (cartItem.getMainMovie().getPrice() * CartCheckOutToDatabase.SITE_CONTENT_SELLING_RATE) / 100;
                                        CheckoutItems checkoutItems = new CheckoutItems();
                                        checkoutItems.setCheckout(checkout);
                                        checkoutItems.setMainMovie(cartItem.getMainMovie());
                                        checkoutItems.setPrice(cartItem.getMainMovie().getPrice());
                                        checkoutItems.setRate(CartCheckOutToDatabase.SITE_CONTENT_SELLING_RATE);
                                        checkoutItems.setOwner_price(cartItem.getMainMovie().getPrice() - site_price);
                                        checkoutItems.setSite_price(site_price);
                                        checkoutItems.setRegistered_at(ontime);
                                        checkoutItems.setRatingType(defaultRatingType);
                                        session.save(checkoutItems);

                                        // delete cart
                                        session.delete(cartItem);
                                    }
                                }

                                tr.commit();
                                responseObject.addProperty("status", true);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            tr.rollback();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
