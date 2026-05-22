package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Cart;
import hibernate.Checkout;
import hibernate.CheckoutItems;
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
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String contentId = request.getParameter("contentId");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", "1");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();

        if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

            Status status = (Status) session.get(Status.class, AddToCart.ACTIVE_STATUS);
            Criteria c1 = session.createCriteria(MainMovie.class);
            c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
            c1.add(Restrictions.eq("status", status));
            MainMovie mainContent = (MainMovie) c1.uniqueResult();

            if (mainContent == null) {
                responseObject.addProperty("message", "Something went wrong. Add to cart content not found! Please refresh the page!");
            } else {
                User user = (User) request.getSession().getAttribute("user");
                if (user != null) {
                    User verifyUser = (User) session.get(User.class, user.getId());
                    if (verifyUser == null) {
                        responseObject.addProperty("message", "Something went wrong. Please sign in again!");
                    } else {
                        if (mainContent.getUser().getId() == verifyUser.getId()) {
                            responseObject.addProperty("message", "Selected content is a your product. You can watch it without purchasing!");
                            responseObject.addProperty("status", "2");
                        } else {

                            Criteria c2 = session.createCriteria(Cart.class);
                            c2.add(Restrictions.eq("user", verifyUser));
                            c2.add(Restrictions.eq("mainMovie", mainContent));
                            List<Cart> cartList = c2.list();

                            if (cartList == null || cartList.size() == 0) {
                                Criteria c3 = session.createCriteria(Checkout.class);
                                c3.add(Restrictions.eq("user", verifyUser));
                                List<Checkout> checkoutList = c3.list();

                                CheckoutItems checkoutItems = null;

                                if (!checkoutList.isEmpty()) {
                                    Criteria c4 = session.createCriteria(CheckoutItems.class);
                                    c4.add(Restrictions.in("checkout", checkoutList));
                                    c4.add(Restrictions.eq("mainMovie", mainContent));
                                    checkoutItems = (CheckoutItems) c4.uniqueResult();
                                }

                                if (checkoutItems != null) {
                                    responseObject.addProperty("message", "Selected content was already purchased!");
                                    responseObject.addProperty("status", "2");
                                } else {
                                    Cart newCart = new Cart();
                                    newCart.setUser(verifyUser);
                                    newCart.setMainMovie(mainContent);
                                    session.save(newCart);
                                    session.beginTransaction().commit();

                                    responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " added to cart successfully!");
                                    responseObject.addProperty("status", "3");
                                }

                            } else {
                                responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " has already been added to cart!");
                                responseObject.addProperty("status", "3");
                            }

                        }
                    }
                } else {
                    HttpSession ses = request.getSession();
                    if (ses.getAttribute("sessionCart") == null) {

                        ArrayList<Cart> sessionCart = new ArrayList();
                        Cart newCart = new Cart();
                        newCart.setUser(null);
                        newCart.setMainMovie(mainContent);
                        sessionCart.add(newCart);
                        ses.setAttribute("sessionCart", sessionCart);

                        responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " added to cart successfully!");
                        responseObject.addProperty("status", "3");
                    } else {

                        ArrayList<Cart> sessionCartList = (ArrayList<Cart>) ses.getAttribute("sessionCart");
                        Cart foundedCart = null;

                        for (Cart cart : sessionCartList) {
                            if (cart.getMainMovie().getId() == mainContent.getId()) {
                                foundedCart = cart;
                                break;
                            }
                        }

                        if (foundedCart != null) {
                            responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " has already been added to cart!");
                            responseObject.addProperty("status", "3");
                        } else {
                            foundedCart = new Cart();
                            foundedCart.setUser(null);
                            foundedCart.setMainMovie(mainContent);
                            sessionCartList.add(foundedCart);
                            ses.setAttribute("sessionCart", sessionCartList);

                            responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " added to cart successfully!");
                            responseObject.addProperty("status", "3");
                        }
                    }
                }
            }

        } else {
            responseObject.addProperty("message", "Invalid Content!");
        }
        session.close();

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
