/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Country;
import hibernate.Genre;
import hibernate.HibernateUtil;
import hibernate.Language;
import hibernate.MainMovie;
import hibernate.MovieType;
import hibernate.QualityType;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static model.Util.isPasswordValid;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "MyAccount", urlPatterns = {"/MyAccount"})
public class MyAccount extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        Gson gson = new Gson();
        HttpSession ses = request.getSession();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (ses != null && ses.getAttribute("user") != null) {
            User user = (User) ses.getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User verifyedUser = (User) session.get(User.class, user.getId());

            if (verifyedUser != null) {

                responseObject.addProperty("firstName", verifyedUser.getFirst_name());
                responseObject.addProperty("lastName", verifyedUser.getLast_name());
                responseObject.addProperty("email", verifyedUser.getEmail());
                responseObject.addProperty("password", verifyedUser.getPassword());

                Criteria c1 = session.createCriteria(MovieType.class);
                List<MovieType> movieTypeList = c1.list();
                responseObject.add("movieTypeList", gson.toJsonTree(movieTypeList));

                Criteria c2 = session.createCriteria(QualityType.class);
                List<QualityType> qualityList = c2.list();
                responseObject.add("qualityList", gson.toJsonTree(qualityList));

                Criteria c3 = session.createCriteria(Genre.class);
                List<Genre> genreList = c3.list();
                responseObject.add("genreList", gson.toJsonTree(genreList));

                Criteria c4 = session.createCriteria(Country.class);
                List<Country> countryList = c4.list();
                responseObject.add("countryList", gson.toJsonTree(countryList));

                Criteria c5 = session.createCriteria(Language.class);
                List<Language> languageList = c5.list();
                responseObject.add("languageList", gson.toJsonTree(languageList));

                Criteria c6 = session.createCriteria(MainMovie.class);
                c6.add(Restrictions.eq("user", verifyedUser));
                c6.addOrder(Order.desc("registered_at"));
                List<MainMovie> userContentList = c6.list();
                responseObject.add("userContentList", gson.toJsonTree(userContentList));

                responseObject.addProperty("status", true);
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String savingType = userData.get("savingType").getAsString();

        if (savingType != null) {

            if (savingType.equals("userDetails")) {

                String firstName = userData.get("firstName").getAsString();
                String lastName = userData.get("lastName").getAsString();

                if (firstName.isEmpty()) {
                    responseObject.addProperty("message", "First name can not be empty!");
                } else if (firstName.length() > 45) {
                    responseObject.addProperty("message", "First name must contain less than 45 characters!");
                } else if (lastName.isEmpty()) {
                    responseObject.addProperty("message", "Last name can not be empty!");
                } else if (lastName.length() > 45) {
                    responseObject.addProperty("message", "Last name must contain less than 45 characters!");
                } else {

                    HttpSession ses = request.getSession();

                    if (ses.getAttribute("user") != null) {
                        User user = (User) ses.getAttribute("user");

                        SessionFactory sf = HibernateUtil.getSessionFactory();
                        Session session = sf.openSession();

                        Criteria c1 = session.createCriteria(User.class);
                        c1.add(Restrictions.eq("email", user.getEmail()));

                        if (!c1.list().isEmpty()) {
                            User u1 = (User) (c1.list().get(0));

                            u1.setFirst_name(firstName);
                            u1.setLast_name(lastName);

                            session.update(u1);
                            session.beginTransaction().commit();

                            ses.setAttribute("user", u1);

                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "User information updated successfully!");
                            session.close();
                        } else {
                            responseObject.addProperty("message", "There is no user with this email(" + user.getEmail() + ")");
                        }

                    } else {
                        responseObject.addProperty("message", "Something went wrong. Please try again later!");
                    }

                }

            } else if (savingType.equals("userPasswordDetails")) {

                String currentPassword = userData.get("currentPassword").getAsString();
                String newPassword = userData.get("newPassword").getAsString();
                String confirmPassword = userData.get("confirmPassword").getAsString();

                if (!isPasswordValid(currentPassword) && currentPassword.length() < 8 && currentPassword.length() > 20) {
                    responseObject.addProperty("message", "Current Password must be contained "
                            + "minimum 8 characters and maximum 20 characters, at least one letter, one "
                            + "number and one special character.");
                } else if (newPassword.isEmpty() && !isPasswordValid(newPassword) || newPassword.length() < 8 || newPassword.length() > 20) {
                    responseObject.addProperty("message", "New Password must be contained "
                            + "minimum 8 characters and maximum 20 characters, at least one letter, one "
                            + "number and one special character.");
                } else if (currentPassword.equals(newPassword)) {
                    responseObject.addProperty("message", "Current password & New password are same.");
                } else if (!newPassword.equals(confirmPassword)) {
                    responseObject.addProperty("message", "New password & Confirm password do not match!");
                } else {

                    HttpSession ses = request.getSession();

                    if (ses.getAttribute("user") != null) {
                        User user = (User) ses.getAttribute("user");

                        SessionFactory sf = HibernateUtil.getSessionFactory();
                        Session session = sf.openSession();

                        Criteria c1 = session.createCriteria(User.class);
                        c1.add(Restrictions.eq("email", user.getEmail()));

                        if (!c1.list().isEmpty()) {
                            User u1 = (User) (c1.list().get(0));

                            if (!confirmPassword.isEmpty()) {
                                u1.setPassword(newPassword);
                            } else {
                                u1.setPassword(currentPassword);
                            }

                            session.update(u1);
                            session.beginTransaction().commit();

                            ses.setAttribute("user", u1);

                            responseObject.addProperty("status", true);
                            responseObject.addProperty("message", "User password updated successfully!");
                            session.close();
                        } else {
                            responseObject.addProperty("message", "There is no user with this email(" + user.getEmail() + ")");
                        }

                    } else {
                        responseObject.addProperty("message", "Something went wrong. Please try again later!");
                    }

                }
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
