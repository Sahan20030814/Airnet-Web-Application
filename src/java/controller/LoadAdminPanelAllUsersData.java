/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.Checkout;
import hibernate.CheckoutItems;
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
@WebServlet(name = "LoadAdminPanelAllUsersData", urlPatterns = {"/LoadAdminPanelAllUsersData"})
public class LoadAdminPanelAllUsersData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String userEmail = request.getParameter("userEmail");
        String contentId = request.getParameter("contentId");

        if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
            Admin admin = (Admin) request.getSession().getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());

            if (verifyedAdmin != null) {
                Criteria c1 = session.createCriteria(User.class);
                c1.add(Restrictions.like("email", userEmail + "%"));
                List<User> userList = c1.list();

                ArrayList<User> allUsersList = new ArrayList();

                if (userList != null && userList.size() > 0) {
                    Criteria c2 = session.createCriteria(Checkout.class);
                    c2.add(Restrictions.in("user", userList));
                    List<Checkout> allCheckoutList = c2.list();

                    if (allCheckoutList != null) {

                        MainMovie content = null;
                        if (contentId != null && !contentId.equals("0") && Util.isInteger(contentId)) {
                            content = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
                        }

                        Criteria c3 = session.createCriteria(CheckoutItems.class);
                        c3.add(Restrictions.in("checkout", allCheckoutList));
                        if (content != null) {
                            c3.add(Restrictions.eq("mainMovie", content));
                        }
                        List<CheckoutItems> allCheckoutItemsList = c3.list();

                        for (CheckoutItems checkoutItems : allCheckoutItemsList) {
                            boolean alreadyExists = false;
                            for (User user : allUsersList) {
                                if (user.getId() == checkoutItems.getCheckout().getUser().getId()) {
                                    alreadyExists = true;
                                }
                            }
                            if (!alreadyExists) {
                                allUsersList.add(checkoutItems.getCheckout().getUser());
                            }
                        }
                    }
                }

                responseObject.addProperty("selectedAllUsersListCount", allUsersList.size());
                responseObject.add("selectedAllUsersList", gson.toJsonTree(allUsersList));

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
