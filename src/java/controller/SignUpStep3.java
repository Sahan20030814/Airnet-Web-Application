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
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "SignUpStep3", urlPatterns = {"/SignUpStep3"})
public class SignUpStep3 extends HttpServlet {

    private static final int VERIFIED_USER_STATUS_ID = 3;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Gson gson = new Gson();

        HttpSession ses = request.getSession();

        if (ses.getAttribute("email") == null) {
            responseObject.addProperty("message", "1");  // Email not found in session!
        } else {
            String email = String.valueOf(ses.getAttribute("email"));

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c1 = session.createCriteria(User.class);
            c1.add(Restrictions.eq("email", email));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "2");  //  Email not found in database!
            } else {

                User u1 = (User) (c1.list().get(0));

                JsonObject verification = gson.fromJson(request.getReader(), JsonObject.class);
                String verification_code = verification.get("verificationCode").getAsString();

                if (!verification_code.equals(u1.getVerification())) {
                    responseObject.addProperty("message", "Incorrect verification code!");
                } else {

                    UserStatus user_status = (UserStatus) session.get(UserStatus.class, SignUpStep3.VERIFIED_USER_STATUS_ID);

                    if (user_status != null) {
                        u1.setUser_status(user_status);
                        session.update(u1);
                        session.beginTransaction().commit();

                        ses.setAttribute("user", u1);
                        responseObject.addProperty("status", true);
                    } else {
                        responseObject.addProperty("message", "Something went wrong!");
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
