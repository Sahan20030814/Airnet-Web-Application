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
import hibernate.MainMovieHasCountry;
import hibernate.MainMovieHasGenre;
import hibernate.MainMovieHasLanguage;
import hibernate.Status;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadCheckoutData", urlPatterns = {"/LoadCheckoutData"})
public class LoadCheckoutData extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        responseObject.addProperty("contentCheckoutStatus", false);

        String contentId = request.getParameter("id");

        if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Status status = (Status) session.get(Status.class, LoadCheckoutData.ACTIVE_STATUS);

            Criteria c1 = session.createCriteria(MainMovie.class);
            c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
            c1.add(Restrictions.eq("status", status));
            MainMovie checkoutContent = (MainMovie) c1.uniqueResult();

            if (checkoutContent != null) {
                HttpSession ses = request.getSession(false);

                if (ses != null && ses.getAttribute("user") != null) {
                    User user = (User) ses.getAttribute("user");

                    User verifiedUser = (User) session.get(User.class, user.getId());

                    if (verifiedUser != null) {

                        if (verifiedUser.getId() == checkoutContent.getUser().getId()) {
                            responseObject.addProperty("contentCheckoutStatus", true);
                        } else {
                            Criteria c2 = session.createCriteria(Checkout.class);
                            c2.add(Restrictions.eq("user", verifiedUser));
                            List<Checkout> checkoutList = c2.list();

                            Criteria c3 = session.createCriteria(CheckoutItems.class);
                            c3.add(Restrictions.in("checkout", checkoutList));
                            c3.add(Restrictions.eq("mainMovie", checkoutContent));
                            CheckoutItems checkoutItems = (CheckoutItems) c3.uniqueResult();

                            if (checkoutItems != null) {
                                responseObject.addProperty("contentCheckoutStatus", true);
                            } else {
                                checkoutContent.setUser(null);
                                responseObject.add("checkoutContent", gson.toJsonTree(checkoutContent));

                                Criteria c4 = session.createCriteria(MainMovieHasGenre.class);
                                c4.add(Restrictions.eq("mainMovie", checkoutContent));
                                List<MainMovieHasGenre> genreList = c4.list();
                                String genre_line = "";

                                for (MainMovieHasGenre mainMovieHasGenre : genreList) {
                                    genre_line += mainMovieHasGenre.getGenre().getName() + ", ";
                                }
                                genre_line = genre_line.replaceAll(",\\s*$", "");
                                responseObject.addProperty("genre_line", genre_line);

                                Criteria c5 = session.createCriteria(MainMovieHasCountry.class);
                                c5.add(Restrictions.eq("mainMovie", checkoutContent));
                                List<MainMovieHasCountry> countryList = c5.list();
                                String country_line = "";

                                for (MainMovieHasCountry mainMovieHasCountry : countryList) {
                                    country_line += mainMovieHasCountry.getCountry().getName() + ", ";
                                }
                                country_line = country_line.replaceAll(",\\s*$", "");
                                responseObject.addProperty("country_line", country_line);

                                Criteria c6 = session.createCriteria(MainMovieHasLanguage.class);
                                c6.add(Restrictions.eq("mainMovie", checkoutContent));
                                List<MainMovieHasLanguage> languageList = c6.list();
                                String language_line = "";

                                for (MainMovieHasLanguage mainMovieHasLanguage : languageList) {
                                    language_line += mainMovieHasLanguage.getLanguage().getName() + ", ";
                                }
                                language_line = language_line.replaceAll(",\\s*$", "");
                                responseObject.addProperty("language_line", language_line);

                                responseObject.addProperty("status", true);
                            }
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
