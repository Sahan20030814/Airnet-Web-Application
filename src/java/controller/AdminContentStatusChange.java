/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.Status;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static model.Util.isInteger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AdminContentStatusChange", urlPatterns = {"/AdminContentStatusChange"})
public class AdminContentStatusChange extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        Gson gson = new Gson();
        responseObject.addProperty("status", false);
        responseObject.addProperty("differentStatus", false);

        String contentId = request.getParameter("contentId");
        String statusId = request.getParameter("statusId");

        if (contentId != null && isInteger(contentId) && statusId != null && isInteger(statusId)) {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();
            Transaction tr = session.beginTransaction();

            try {
                MainMovie content = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));

                if (content != null) {
                    if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
                        Admin admin = (Admin) request.getSession().getAttribute("admin");

                        Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());
                        if (verifyedAdmin != null) {

                            if (content.getStatus().getId() != Integer.parseInt(statusId)) {
                                Status status = (Status) session.get(Status.class, Integer.parseInt(statusId));
                                if (status != null) {
                                    content.setStatus(status);
                                    session.update(content);
                                    responseObject.addProperty("differentStatus", true);
                                    responseObject.addProperty("status", true);
                                }

                                session.flush();
                                session.clear();
                                tr.commit();
                            } else {
                                responseObject.addProperty("status", true);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                tr.rollback();
            } finally {
                session.close();
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
