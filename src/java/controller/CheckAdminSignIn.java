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
@WebServlet(name = "CheckAdminSignIn", urlPatterns = {"/CheckAdminSignIn"})
public class CheckAdminSignIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession ses = request.getSession(false);

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (ses != null && ses.getAttribute("admin") != null) {

            Admin admin = (Admin) ses.getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c1 = session.createCriteria(Admin.class);
            c1.add(Restrictions.eq("email", admin.getEmail()));
            c1.add(Restrictions.eq("password", admin.getPassword()));
            Admin a1 = (Admin) c1.uniqueResult();

            if (a1 != null) {
                responseObject.add("admin", gson.toJsonTree(admin));
                responseObject.addProperty("status", true);
            } else {
                ses.invalidate();
            }

            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
