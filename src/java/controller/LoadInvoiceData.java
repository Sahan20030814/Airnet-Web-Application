/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Checkout;
import hibernate.CheckoutItems;
import hibernate.HibernateUtil;
import hibernate.User;
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
@WebServlet(name = "LoadInvoiceData", urlPatterns = {"/LoadInvoiceData"})
public class LoadInvoiceData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        String invoiceId = request.getParameter("invoiceId");
        if (invoiceId != null && !invoiceId.isEmpty() && Util.isInteger(invoiceId)) {

            HttpSession ses = request.getSession(false);

            if (ses != null && ses.getAttribute("user") != null) {
                User user = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();

                User verifiedUser = (User) session.get(User.class, user.getId());
                if (verifiedUser != null) {

                    Criteria c1 = session.createCriteria(Checkout.class);
                    c1.add(Restrictions.eq("id", Integer.parseInt(invoiceId)));
                    c1.add(Restrictions.eq("user", verifiedUser));
                    Checkout checkout = (Checkout) c1.uniqueResult();
                    responseObject.add("checkoutList", gson.toJsonTree(checkout));

                    Criteria c2 = session.createCriteria(CheckoutItems.class);
                    c2.add(Restrictions.eq("checkout", checkout));
                    List<CheckoutItems> checkoutItemsList = c2.list();
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
