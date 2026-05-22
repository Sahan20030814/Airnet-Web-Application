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
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AdminVerification", urlPatterns = {"/AdminVerification"})
public class AdminVerification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Gson gson = new Gson();

        HttpSession ses = request.getSession();

        if (ses.getAttribute("adminEmail") == null) {
            responseObject.addProperty("message", "1");  // Email not found in session!
        } else {
            String email = String.valueOf(ses.getAttribute("adminEmail"));
            String rememberMe = String.valueOf(ses.getAttribute("adminRememberMe"));

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c1 = session.createCriteria(Admin.class);
            c1.add(Restrictions.eq("email", email));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "2");  //  Email not found in database!
            } else {

                Admin a = (Admin) (c1.list().get(0));

                JsonObject verification = gson.fromJson(request.getReader(), JsonObject.class);
                String verification_code = verification.get("verificationCode").getAsString();

                if (!verification_code.equals(a.getVerification())) {
                    responseObject.addProperty("message", "Incorrect verification code!");
                } else {
                    ses.setAttribute("admin", a);

                    if (rememberMe != null) {
                        if (rememberMe.equals("true")) {
                            ses.setMaxInactiveInterval(172800);   // 2 days
                        }
                    }

                    responseObject.addProperty("status", true);
                }
            }

            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
