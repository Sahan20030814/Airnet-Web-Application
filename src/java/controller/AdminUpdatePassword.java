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
import static model.Util.isPasswordValid;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AdminUpdatePassword", urlPatterns = {"/AdminUpdatePassword"})
public class AdminUpdatePassword extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String newPassword = userData.get("newPassword").getAsString();
        String confirmPassword = userData.get("confirmPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (newPassword.isEmpty()) {
            responseObject.addProperty("message", "New password can not be empty!");
        } else if (!isPasswordValid(newPassword) || newPassword.length() < 8 || newPassword.length() > 20) {
            responseObject.addProperty("message", "New password must contain "
                    + "minimum 8 - 20 characters, at least one letter, one "
                    + "number and one special character without spaces.");
        } else if (confirmPassword.isEmpty()) {
            responseObject.addProperty("message", "Confirm password can not be empty!");
        } else if (!newPassword.equals(confirmPassword)) {
            responseObject.addProperty("message", "New password & Confirm password do not match!");
        } else {

            HttpSession ses = request.getSession();

            if (ses.getAttribute("adminEmail") == null) {
                responseObject.addProperty("message", "1");
            } else {

                String email = String.valueOf(ses.getAttribute("adminEmail"));

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();

                Criteria c1 = session.createCriteria(Admin.class);
                c1.add(Restrictions.eq("email", email));

                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message", "2");  //  Email not found in database!
                } else {

                    Admin a = (Admin) (c1.list().get(0));
                    a.setPassword(newPassword);

                    session.merge(a);
                    session.beginTransaction().commit();

                    request.getSession().setAttribute("email", email);
                    responseObject.addProperty("message", "Airnet admin password updated successful!");
                    responseObject.addProperty("status", true);
                }
                session.close();
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
