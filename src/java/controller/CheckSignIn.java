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
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "CheckSignIn", urlPatterns = {"/CheckSignIn"})
public class CheckSignIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        HttpSession ses = request.getSession(false);

        Gson gson = new Gson();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (ses != null && ses.getAttribute("user") != null) {

            User user = (User) ses.getAttribute("user");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c1 = session.createCriteria(User.class);
            c1.add(Restrictions.eq("email", user.getEmail()));
            c1.add(Restrictions.eq("password", user.getPassword()));
            User u1 = (User) c1.uniqueResult();

            if (u1 != null) {
                responseObject.add("user", gson.toJsonTree(user));
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
