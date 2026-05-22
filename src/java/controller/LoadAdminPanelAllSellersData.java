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
import hibernate.User;
import java.io.IOException;
import java.util.ArrayList;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadAdminPanelAllSellersData", urlPatterns = {"/LoadAdminPanelAllSellersData"})
public class LoadAdminPanelAllSellersData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String sellerEmail = request.getParameter("sellerEmail");
        String contentId = request.getParameter("contentId");

        if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
            Admin admin = (Admin) request.getSession().getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());

            if (verifyedAdmin != null) {
                Criteria c1 = session.createCriteria(User.class);
                c1.add(Restrictions.like("email", sellerEmail + "%"));
                List<User> sellerList = c1.list();

                ArrayList<User> allSellersList = new ArrayList();
                if (sellerList != null && sellerList.size() > 0) {
                    Criteria c2 = session.createCriteria(MainMovie.class);
                    c2.add(Restrictions.in("user", sellerList));
                    if (contentId != null && !contentId.equals("0") && Util.isInteger(contentId)) {
                        c2.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                    }
                    List<MainMovie> allContentList = c2.list();

                    for (MainMovie mainMovie : allContentList) {
                        boolean alreadyExists = false;
                        for (User user : allSellersList) {
                            if (user.getId() == mainMovie.getUser().getId()) {
                                alreadyExists = true;
                            }
                        }
                        if (!alreadyExists) {
                            allSellersList.add(mainMovie.getUser());
                        }
                    }
                }

                responseObject.addProperty("selectedAllSellersListCount", allSellersList.size());
                responseObject.add("selectedAllSellersList", gson.toJsonTree(allSellersList));

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
