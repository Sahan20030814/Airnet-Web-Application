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
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import static model.Util.getReleaseDateObject;
import static model.Util.isValidDate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadAdminPanelAllInvoicesData", urlPatterns = {"/LoadAdminPanelAllInvoicesData"})
public class LoadAdminPanelAllInvoicesData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        String orderType = request.getParameter("orderType");
        String userId = request.getParameter("userId");
        String contentId = request.getParameter("contentId");

        if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
            Admin admin = (Admin) request.getSession().getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());

            if (verifyedAdmin != null) {

                User user = null;
                if (userId != null && !userId.equals("0") && Util.isInteger(userId)) {
                    user = (User) session.get(User.class, Integer.parseInt(userId));
                }

                Criteria c1 = session.createCriteria(Checkout.class);
                if (user != null) {
                    c1.add(Restrictions.eq("user", user));
                }
                if (dateFrom != null && !dateFrom.isEmpty() && isValidDate(dateFrom)) {
                    Date dateFrom_date = getReleaseDateObject(dateFrom);
                    if (dateFrom_date != null) {
                        c1.add(Restrictions.ge("registered_at", dateFrom_date));
                    }
                }
                if (dateTo != null && !dateTo.isEmpty() && isValidDate(dateTo)) {
                    Date dateTo_date = getReleaseDateObject(dateTo);
                    if (dateTo_date != null) {
                        c1.add(Restrictions.le("registered_at", dateTo_date));
                    }
                }
                List<CheckOut> checkoutList = c1.list();

                ArrayList<CheckoutItems> checkoutItemsList = new ArrayList();
                if (checkoutList != null && checkoutList.size() > 0) {
                    MainMovie content = null;
                    if (contentId != null && !contentId.equals("0") && Util.isInteger(contentId)) {
                        content = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
                    }

                    Criteria c2 = session.createCriteria(CheckoutItems.class);
                    c2.add(Restrictions.in("checkout", checkoutList));
                    if (content != null) {
                        c2.add(Restrictions.eq("mainMovie", content));
                    }
                    if (orderType != null && Util.isInteger(orderType)) {
                        if (orderType.equals("1")) {
                            c2.addOrder(Order.asc("registered_at"));
                        } else if (orderType.equals("2")) {
                            c2.addOrder(Order.desc("registered_at"));
                        }
                    }

                    checkoutItemsList = (ArrayList<CheckoutItems>) c2.list();
                }

                responseObject.addProperty("selectedAllInvoicesListCount", checkoutItemsList.size());
                responseObject.add("selectedAllInvoicesList", gson.toJsonTree(checkoutItemsList));

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
