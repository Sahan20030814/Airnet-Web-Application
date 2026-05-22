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
import hibernate.MovieType;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "LoadAdminPanelDashBoardData", urlPatterns = {"/LoadAdminPanelDashBoardData"})
public class LoadAdminPanelDashBoardData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        Gson gson = new Gson();

        if (request.getSession() != null && request.getSession().getAttribute("admin") != null) {
            Admin admin = (Admin) request.getSession().getAttribute("admin");

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Admin verifyedAdmin = (Admin) session.get(Admin.class, admin.getId());

            if (verifyedAdmin != null) {
                Criteria c1 = session.createCriteria(MainMovie.class);
                c1.addOrder(Order.desc("registered_at"));
                List<MainMovie> allContentList = c1.list();
                responseObject.addProperty("allContentCount", c1.list().size());

                Date firstDateOfCurrentMonth = Util.getFirstDateOfCurrentMonth();

                Criteria c2 = session.createCriteria(MainMovie.class);
                c2.add(Restrictions.ge("registered_at", firstDateOfCurrentMonth));
                responseObject.addProperty("currentMonthContentCount", c2.list().size());

                ArrayList<User> allSellersList = new ArrayList();
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
                responseObject.addProperty("allSellersCount", allSellersList.size());

                ArrayList<User> currentMonthSellersList = new ArrayList();
                for (User user : allSellersList) {
                    if (!user.getRegistered_at().before(firstDateOfCurrentMonth)) { // means givenDate >= firstDate
                        currentMonthSellersList.add(user);
                    }
                }
                responseObject.addProperty("currentMonthSellersCount", currentMonthSellersList.size());

                Criteria c3 = session.createCriteria(Checkout.class);
                c3.addOrder(Order.asc("registered_at"));
                List<Checkout> allCheckoutList = c3.list();

                ArrayList<Checkout> allCheckoutUsersList = new ArrayList();
                for (Checkout checkout : allCheckoutList) {
                    boolean alreadyExists = false;
                    for (Checkout userCheckout : allCheckoutUsersList) {
                        if (userCheckout.getUser().getId() == checkout.getUser().getId()) {
                            alreadyExists = true;
                        }
                    }
                    if (!alreadyExists) {
                        allCheckoutUsersList.add(checkout);
                    }
                }
                responseObject.addProperty("allUsersCount", allCheckoutUsersList.size());

                ArrayList<Checkout> currentMonthCheckoutUsersList = new ArrayList();
                for (Checkout checkout : allCheckoutUsersList) {
                    if (!checkout.getRegistered_at().before(firstDateOfCurrentMonth)) { // means givenDate >= firstDate
                        currentMonthCheckoutUsersList.add(checkout);
                    }
                }
                responseObject.addProperty("currentMonthUsersCount", currentMonthCheckoutUsersList.size());

                responseObject.addProperty("allInvoicesCount", allCheckoutList.size());

                ArrayList<CheckoutItems> allCheckoutItemsList = new ArrayList();
                if (allCheckoutList.size() > 0) {
                    Criteria c4 = session.createCriteria(CheckoutItems.class);
                    c4.add(Restrictions.in("checkout", allCheckoutList));
                    allCheckoutItemsList = (ArrayList<CheckoutItems>) c4.list();
                }

                double all_total_income = 0;
                double all_sellers_income = 0;
                double all_site_profit = 0;

                for (CheckoutItems checkoutItems : allCheckoutItemsList) {
                    all_total_income += checkoutItems.getPrice();
                    all_sellers_income += checkoutItems.getOwner_price();
                    all_site_profit += checkoutItems.getSite_price();
                }
                responseObject.addProperty("all_total_income", all_total_income);
                responseObject.addProperty("all_sellers_income", all_sellers_income);
                responseObject.addProperty("all_site_profit", all_site_profit);

                ArrayList<Checkout> currentMonthCheckoutList = new ArrayList();
                for (Checkout checkout : allCheckoutList) {
                    if (!checkout.getRegistered_at().before(firstDateOfCurrentMonth)) { // means givenDate >= firstDate
                        currentMonthCheckoutList.add(checkout);
                    }
                }
                responseObject.addProperty("currentMonthInvoicesCount", currentMonthCheckoutList.size());

                ArrayList<CheckoutItems> currentMonthCheckoutItemsList = new ArrayList();
                if (currentMonthCheckoutList.size() > 0) {
                    Criteria c4 = session.createCriteria(CheckoutItems.class);
                    c4.add(Restrictions.in("checkout", currentMonthCheckoutList));
                    currentMonthCheckoutItemsList = (ArrayList<CheckoutItems>) c4.list();
                }

                double current_month_total_income = 0;
                double current_month_sellers_income = 0;
                double current_month_site_profit = 0;

                for (CheckoutItems checkoutItems : currentMonthCheckoutItemsList) {
                    current_month_total_income += checkoutItems.getPrice();
                    current_month_sellers_income += checkoutItems.getOwner_price();
                    current_month_site_profit += checkoutItems.getSite_price();
                }
                responseObject.addProperty("current_month_total_income", current_month_total_income);
                responseObject.addProperty("current_month_sellers_income", current_month_sellers_income);
                responseObject.addProperty("current_month_site_profit", current_month_site_profit);

                Criteria c5 = session.createCriteria(MovieType.class);
                List<MovieType> movieTypeList = c5.list();
                responseObject.add("movieTypeList", gson.toJsonTree(movieTypeList));

                responseObject.add("allSellersList", gson.toJsonTree(allSellersList));
                responseObject.add("allContentList", gson.toJsonTree(allContentList));
                responseObject.add("allCheckoutUsersList", gson.toJsonTree(allCheckoutUsersList));

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
