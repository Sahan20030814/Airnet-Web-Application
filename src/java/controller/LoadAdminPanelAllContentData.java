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
import hibernate.MovieType;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadAdminPanelAllContentData", urlPatterns = {"/LoadAdminPanelAllContentData"})
public class LoadAdminPanelAllContentData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String contentName = request.getParameter("contentName");
        String typeId = request.getParameter("typeId");
        String sellerId = request.getParameter("sellerId");

        if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
            Admin admin = (Admin) request.getSession().getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());

            if (verifyedAdmin != null) {
                MovieType movieType = null;
                if (typeId != null && !typeId.equals("0") && Util.isInteger(typeId)) {
                    movieType = (MovieType) session.get(MovieType.class, Integer.parseInt(typeId));
                }

                User seller = null;
                if (sellerId != null && !sellerId.equals("0") && Util.isInteger(sellerId)) {
                    seller = (User) session.get(User.class, Integer.parseInt(sellerId));
                }

                Criteria c1 = session.createCriteria(MainMovie.class);
                if (movieType != null) {
                    c1.add(Restrictions.eq("movieType", movieType));
                }
                if (seller != null) {
                    c1.add(Restrictions.eq("user", seller));
                }
                c1.add(Restrictions.like("name", contentName + "%"));
                c1.addOrder(Order.desc("registered_at"));
                List<MainMovie> selectedAllContentList = c1.list();

                responseObject.addProperty("selectedAllContentListCount", c1.list().size());
                responseObject.add("selectedAllContentList", gson.toJsonTree(selectedAllContentList));

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
