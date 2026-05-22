package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Checkout;
import hibernate.CheckoutItems;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.RatingType;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AddContentRate", urlPatterns = {"/AddContentRate"})
public class AddContentRate extends HttpServlet {

    private static final int DEFAULT_RATING_TYPE_ID = 0;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String contentId = request.getParameter("contentId");
        String rateTypeId = request.getParameter("rateTypeId");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();

        try {

            if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

                User user = (User) request.getSession().getAttribute("user");
                if (user != null) {
                    User verifyUser = (User) session.get(User.class, user.getId());
                    if (verifyUser == null) {
                        responseObject.addProperty("message", "Something went wrong. Please sign in again!");
                    } else {

                        Criteria c1 = session.createCriteria(MainMovie.class);
                        c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                        MainMovie mainContent = (MainMovie) c1.uniqueResult();

                        if (mainContent == null) {
                            responseObject.addProperty("message", "Something went wrong. Please refresh the page!");
                        } else {

                            if (mainContent.getUser().getId() == verifyUser.getId()) {
                                responseObject.addProperty("message", "You can't rate your own product!");
                            } else {

                                Criteria c2 = session.createCriteria(Checkout.class);
                                c2.add(Restrictions.eq("user", verifyUser));
                                List<Checkout> checkoutList = c2.list();

                                Criteria c3 = session.createCriteria(CheckoutItems.class);
                                c3.add(Restrictions.in("checkout", checkoutList));
                                c3.add(Restrictions.eq("mainMovie", mainContent));
                                CheckoutItems checkoutItems = (CheckoutItems) c3.uniqueResult();

                                if (rateTypeId != null && !rateTypeId.isEmpty() && Util.isInteger(rateTypeId)) {
                                    RatingType ratingType = (RatingType) session.get(RatingType.class, Integer.parseInt(rateTypeId));

                                    if (ratingType == null) {
                                        responseObject.addProperty("message", "Something went wrong. Please refresh the page!");
                                    } else {
                                        if (checkoutItems != null) {
                                            if (checkoutItems.getRatingType().getId() == 0) {
                                                checkoutItems.setRatingType(ratingType);
                                            } else if (checkoutItems.getRatingType().getId() == 1) {
                                                if (rateTypeId.equals("1")) {
                                                    RatingType defaultRatingType = (RatingType) session.get(RatingType.class, AddContentRate.DEFAULT_RATING_TYPE_ID);
                                                    checkoutItems.setRatingType(defaultRatingType);
                                                } else {
                                                    checkoutItems.setRatingType(ratingType);
                                                }
                                            } else if (checkoutItems.getRatingType().getId() == 2) {
                                                if (rateTypeId.equals("2")) {
                                                    RatingType defaultRatingType = (RatingType) session.get(RatingType.class, AddContentRate.DEFAULT_RATING_TYPE_ID);
                                                    checkoutItems.setRatingType(defaultRatingType);
                                                } else {
                                                    checkoutItems.setRatingType(ratingType);
                                                }
                                            }
                                            session.update(checkoutItems);
                                            session.flush();
                                            session.clear();
                                            tr.commit();

                                            responseObject.addProperty("message", "Your valuable rating was stored successfully!");
                                            responseObject.addProperty("status", true);
                                        } else {
                                            responseObject.addProperty("message", "You can't rate this content without purchasing!");
                                        }
                                    }
                                } else {
                                    responseObject.addProperty("message", "Something went wrong. Please refresh the page!");
                                }
                            }
                        }
                    }
                } else {
                    responseObject.addProperty("message", "You can't rate this content without sign in!");
                }
            } else {
                responseObject.addProperty("message", "Something went wrong. Please refresh the page!");
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
