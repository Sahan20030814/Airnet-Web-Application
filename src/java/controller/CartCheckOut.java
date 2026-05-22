/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.Checkout;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.RatingType;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.PayHere;
import model.Util;
import static model.Util.generateUniqueId;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import urlProvider.PublicURLProvider;

/**
 *
 * @author sahan
 */
@WebServlet(name = "CartCheckOut", urlPatterns = {"/CartCheckOut"})
public class CartCheckOut extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;
    private static final int DEFAULT_RATING_TYPE_ID = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        String contentIds = requJsonObject.get("content_ids").getAsString();

        String[] ids = contentIds.split(",");

        // Step 2: Add trimmed words to ArrayList
        ArrayList<String> cartContentIdList = new ArrayList<>();
        for (String id : ids) {
            cartContentIdList.add(id.trim()); // remove extra spaces
        }

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            responseObject.addProperty("message", "Session expired! Please sign in again.");
        } else {
            User verifyUser = (User) session.get(User.class, user.getId());

            if (verifyUser == null) {
                responseObject.addProperty("message", "Something went wrong. Please sign in again!");
            } else {
                if (cartContentIdList.size() > 0) {
                    Status status = (Status) session.get(Status.class, CartCheckOut.ACTIVE_STATUS);
                    ArrayList<Cart> cartList = new ArrayList();
                    boolean canContinue = false;

                    for (String contentId : cartContentIdList) {
                        if (Util.isInteger(contentId)) {
                            Criteria c1 = session.createCriteria(MainMovie.class);
                            c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                            c1.add(Restrictions.eq("status", status));
                            MainMovie checkoutContent = (MainMovie) c1.uniqueResult();

                            if (checkoutContent == null) {
                                responseObject.addProperty("message", "One of selected contents is invalid. Please reload the page!");
                                canContinue = false;
                                break;
                            } else {
                                Criteria c2 = session.createCriteria(Cart.class);
                                c2.add(Restrictions.eq("user", verifyUser));
                                c2.add(Restrictions.eq("mainMovie", checkoutContent));
                                Cart cart = (Cart) c2.uniqueResult();

                                if (cart == null) {
                                    responseObject.addProperty("message", "Something went wrong. Please reload the page!");
                                    canContinue = false;
                                    break;
                                } else {
                                    cartList.add(cart);
                                    canContinue = true;
                                }
                            }

                        } else {
                            responseObject.addProperty("message", "One of selected contents is invalid. Please reload the page!");
                            canContinue = false;
                            break;
                        }
                    }

                    if (canContinue && cartList.size() > 0) {
                        processCheckout(session, tr, verifyUser, cartList, responseObject);
                    }
                } else {
                    responseObject.addProperty("message", "Please select the purchasing content first!");
                }
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

    private void processCheckout(Session session, Transaction tr, User user, ArrayList<Cart> cartContentList, JsonObject responseObject) {
        try {
            int uniqueId;

            RatingType defaultRatingType = (RatingType) session.get(RatingType.class, CartCheckOut.DEFAULT_RATING_TYPE_ID);

            if (defaultRatingType == null) {
                responseObject.addProperty("message", "Something went wrong. Please try again later!");
            } else {

                do {
                    uniqueId = generateUniqueId();
                } while ((Checkout) session.get(Checkout.class, uniqueId) != null);

                int orderId = uniqueId;

                String items = "";
                double amount = 0;

                for (Cart cart : cartContentList) {
                    items += cart.getMainMovie().getName() + ", ";
                    amount += cart.getMainMovie().getPrice();
                }
                items = items.replaceFirst(",\\s*$", "");

                //PayHere process
                String merahantID = "1231405";
                String merchantSecret = "MTY1NzgyMzkwMDEzODEyMTAwNTUxOTYwMzY0NzI2NDQzMTY2ODE=";
                String orderID = "#000" + orderId;
                String currency = "LKR";
                String formattedAmount = new DecimalFormat("0.00").format(amount);
                String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);

                String hash = PayHere.generateMD5(merahantID + orderID + formattedAmount + currency + merchantSecretMD5);

                JsonObject payHereJson = new JsonObject();
                payHereJson.addProperty("sandbox", true);
                payHereJson.addProperty("merchant_id", merahantID);

                payHereJson.addProperty("return_url", "");
                payHereJson.addProperty("cancel_url", "");
                payHereJson.addProperty("notify_url", PublicURLProvider.PUBLIC_URL + "/Airnet/VerifyPayment");

                payHereJson.addProperty("order_id", orderID);
                payHereJson.addProperty("items", items);
                payHereJson.addProperty("amount", formattedAmount);
                payHereJson.addProperty("currency", currency);
                payHereJson.addProperty("hash", hash);

                payHereJson.addProperty("first_name", user.getFirst_name());
                payHereJson.addProperty("last_name", user.getLast_name());
                payHereJson.addProperty("email", user.getEmail());

                payHereJson.addProperty("phone", "");
                payHereJson.addProperty("address", "");
                payHereJson.addProperty("city", "");
                payHereJson.addProperty("country", "");

                responseObject.addProperty("invoiceId", orderId);
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Checkout completed successfully!");
                responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));
            }

        } catch (Exception e) {
            tr.rollback();
        }
    }
}
