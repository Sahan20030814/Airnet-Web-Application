/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import hibernate.UserStatus;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static model.Util.isEmailValid;
import static model.Util.isPasswordValid;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "SignUpStep1", urlPatterns = {"/SignUpStep1"})
public class SignUpStep1 extends HttpServlet {

    private static final int PENDING_USER_STATUS_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String first_name = user.get("firstName").getAsString();
        String last_name = user.get("lastName").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (first_name.isEmpty()) {
            responseObject.addProperty("notification", "First name can not be empty!");
        } else if (first_name.length() > 45) {
            responseObject.addProperty("notification", "First name must contain less than 45 characters!");
        } else if (last_name.isEmpty()) {
            responseObject.addProperty("notification", "Last name can not be empty!");
        } else if (last_name.length() > 45) {
            responseObject.addProperty("notification", "Last name must contain less than 45 characters!");
        } else if (email.isEmpty()) {
            responseObject.addProperty("notification", "Email address can not be empty!");
        } else if (email.length() > 50) {
            responseObject.addProperty("notification", "Email address must contain less than 50 characters!");
        } else if (!isEmailValid(email)) {
            responseObject.addProperty("notification", "Invalid email address!");
        } else if (password.isEmpty()) {
            responseObject.addProperty("notification", "Password can not be empty!");
        } else if (!isPasswordValid(password) || password.length() < 8 || password.length() > 20) {
            responseObject.addProperty("notification", "Password must contain "
                    + "minimum 8 - 20 characters, at least one letter, one "
                    + "number and one special character without spaces.");
        } else {

            // hibernate save
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c = session.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));

            if (!c.list().isEmpty()) {

                User u1 = (User) (c.list().get(0));

                if (u1.getUser_status().getName().equalsIgnoreCase("Verified")) {
                    responseObject.addProperty("notification", "This email address is already registered. Please login to your account!");
                } else {
                    responseObject.addProperty("status", true);
                }

            } else {

                UserStatus user_status = (UserStatus) session.get(UserStatus.class, SignUpStep1.PENDING_USER_STATUS_ID);

                if (user_status != null) {
                    User u = new User();
                    u.setFirst_name(first_name);
                    u.setLast_name(last_name);
                    u.setEmail(email);
                    u.setPassword(password);
                    u.setVerification("pending");
                    u.setRegistered_at(new Date());
                    u.setUser_status(user_status);

                    session.save(u);
                    session.beginTransaction().commit();
                    // hibernate save

                    responseObject.addProperty("status", true);
                } else {
                    responseObject.addProperty("notification", "Something went wrong!");
                }

            }

            // session management
            HttpSession ses = request.getSession();
            ses.setAttribute("email", email);
            // session management

            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

}
