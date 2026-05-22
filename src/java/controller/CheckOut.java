/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Checkout;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.RatingType;
import hibernate.Status;
import hibernate.User;
import java.io.IOException;
import java.text.DecimalFormat;
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
@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;
    private static final int DEFAULT_RATING_TYPE_ID = 0;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        String contentId = requJsonObject.get("content_id").getAsString();

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
                if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

                    Status status = (Status) session.get(Status.class, CheckOut.ACTIVE_STATUS);
                    Criteria c1 = session.createCriteria(MainMovie.class);
                    c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                    c1.add(Restrictions.eq("status", status));
                    MainMovie checkoutContent = (MainMovie) c1.uniqueResult();

                    if (checkoutContent == null) {
                        responseObject.addProperty("message", "Invalid Content!");
                    } else {
                        if (checkoutContent.getPrice() > 0) {
                            processCheckout(session, tr, verifyUser, checkoutContent, responseObject);
                        } else {
                            responseObject.addProperty("message", "This content is not for sale. Sorry for your inconvenience!");
                        }
                    }
                } else {
                    responseObject.addProperty("message", "Invalid Content!");
                }
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

    private void processCheckout(Session session, Transaction tr, User user, MainMovie checkoutContent, JsonObject responseObject) {

        try {
            int uniqueId;
            RatingType defaultRatingType = (RatingType) session.get(RatingType.class, CheckOut.DEFAULT_RATING_TYPE_ID);

            if (defaultRatingType == null) {
                responseObject.addProperty("message", "Something went wrong. Please try again later!");
            } else {

                do {
                    uniqueId = generateUniqueId();
                } while ((Checkout) session.get(Checkout.class, uniqueId) != null);

                int orderId = uniqueId;

                String items = checkoutContent.getName();
                double amount = checkoutContent.getPrice();

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
