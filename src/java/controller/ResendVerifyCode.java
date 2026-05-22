/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Mail;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "ResendVerifyCode", urlPatterns = {"/ResendVerifyCode"})
public class ResendVerifyCode extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", "0");

        final String email = String.valueOf(request.getSession().getAttribute("email"));

        if (email != null && email != "null") {

            // hibernate save
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c = session.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));

            if (c.list().isEmpty()) {
                responseObject.addProperty("status", "1");
            } else {

                User u1 = (User) c.list().get(0);

                if (u1.getUser_status().getName().equalsIgnoreCase("Verified")) {
                    responseObject.addProperty("status", "2");
                } else {

                    final String verification_code = Util.generateCode();

                    u1.setVerification(verification_code);

                    session.update(u1);
                    session.beginTransaction().commit();
                    // hibernate update

                    // send email
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Mail.sendMail(email, "AIRNET - Account Verification", "<h1>Your verification code: <span style='color:red;'>" + verification_code + "</span></h1>");
                        }
                    }).start();
                    // send email

                    // session management
                    request.getSession().setAttribute("email", email);
                    // session management

                    responseObject.addProperty("status", "3");
                }

            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);

    }

}
