/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
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
@WebServlet(name = "AdminResendForgotPasswordVerification", urlPatterns = {"/AdminResendForgotPasswordVerification"})
public class AdminResendForgotPasswordVerification extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", "0");

        final String email = String.valueOf(request.getSession().getAttribute("adminEmail"));
        if (email != null && email != "null") {

            // hibernate save
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c = session.createCriteria(Admin.class);
            c.add(Restrictions.eq("email", email));

            if (c.list().isEmpty()) {
                responseObject.addProperty("status", "1");
            } else {
                Admin a = (Admin) c.list().get(0);

                final String verification_code = Util.generateCode();
                a.setVerification(verification_code);

                session.update(a);
                session.beginTransaction().commit();
                // hibernate update

                // send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "AIRNET - Admin Account Verification", "<h1>Admin account verification code: <span style='color:red;'>" + verification_code + "</span></h1>");
                    }
                }).start();
                // send email

                // session management
                request.getSession().setAttribute("adminEmail", email);
                // session management

                responseObject.addProperty("status", "2");
            }
            session.close();
        }
        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
