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
@WebServlet(name = "LoadAdminPanelPurchasingItemsData", urlPatterns = {"/LoadAdminPanelPurchasingItemsData"})
public class LoadAdminPanelPurchasingItemsData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String userId = request.getParameter("userId");

        if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
            Admin admin = (Admin) request.getSession().getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());

            if (verifyedAdmin != null) {

                ArrayList<CheckoutItems> allPurchasedItemsList = new ArrayList();

                if (userId != null && !userId.equals("0") && Util.isInteger(userId)) {
                    User user = (User) session.get(User.class, Integer.parseInt(userId));

                    if (user != null) {
                        Criteria c1 = session.createCriteria(Checkout.class);
                        c1.add(Restrictions.eq("user", user));
                        List<Checkout> allCheckoutList = c1.list();

                        if (allCheckoutList != null && allCheckoutList.size() > 0) {
                            Criteria c2 = session.createCriteria(CheckoutItems.class);
                            c2.add(Restrictions.in("checkout", allCheckoutList));
                            allPurchasedItemsList = (ArrayList<CheckoutItems>) c2.list();
                        }
                    }

                    responseObject.addProperty("specificPurchasingItemsListCount", allPurchasedItemsList.size());
                    responseObject.add("specificPurchasingItemsList", gson.toJsonTree(allPurchasedItemsList));

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
