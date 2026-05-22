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
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadAdminInvoiceData", urlPatterns = {"/LoadAdminInvoiceData"})
public class LoadAdminInvoiceData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId != null && !invoiceId.isEmpty() && Util.isInteger(invoiceId)) {

            HttpSession ses = request.getSession(false);

            if (ses != null && ses.getAttribute("admin") != null) {
                Admin admin = (Admin) ses.getAttribute("admin");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();

                Admin verifiedAdmin = (Admin) session.get(Admin.class, admin.getId());
                if (verifiedAdmin != null) {

                    Checkout checkout = (Checkout) session.get(Checkout.class, Integer.parseInt(invoiceId));
                    responseObject.add("checkoutList", gson.toJsonTree(checkout));

                    Criteria c1 = session.createCriteria(CheckoutItems.class);
                    c1.add(Restrictions.eq("checkout", checkout));
                    List<CheckoutItems> checkoutItemsList = c1.list();
                    responseObject.add("checkoutItemsList", gson.toJsonTree(checkoutItemsList));

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
