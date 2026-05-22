package model;

import hibernate.Checkout;
import hibernate.CheckoutItems;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static model.Util.isInteger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@WebFilter(urlPatterns = {"/buying_content_view.html"})
public class CheckBuyingContentViewFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        String contentId = req.getParameter("contentId");

        if (contentId == null || contentId.isEmpty() || !isInteger(contentId)) {
            res.sendRedirect("index.html");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            MainMovie mainContent = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
            if (mainContent != null) {

                HttpSession ses = req.getSession(false);
                if (ses != null && ses.getAttribute("user") != null) {
                    User user = (User) ses.getAttribute("user");

                    User verifyUser = (User) session.get(User.class, user.getId());
                    if (verifyUser != null) {

                        if (mainContent.getUser().getId() == verifyUser.getId()) {
                            chain.doFilter(request, response);
                        } else {
                            Criteria c1 = session.createCriteria(Checkout.class);
                            c1.add(Restrictions.eq("user", verifyUser));
                            List<Checkout> checkoutList = c1.list();

                            if (checkoutList != null && !checkoutList.isEmpty()) {

                                Criteria c2 = session.createCriteria(CheckoutItems.class);
                                c2.add(Restrictions.in("checkout", checkoutList));
                                c2.add(Restrictions.eq("mainMovie", mainContent));
                                CheckoutItems checkoutItem = (CheckoutItems) c2.uniqueResult();

                                if (checkoutItem != null) {
                                    chain.doFilter(request, response);
                                } else {
                                    res.sendRedirect("single_product_view.html?id=" + contentId);
                                }

                            } else {
                                res.sendRedirect("single_product_view.html?id=" + contentId);
                            }
                        }

                    } else {
                        res.sendRedirect("single_product_view.html?id=" + contentId);
                    }

                } else {
                    res.sendRedirect("single_product_view.html?id=" + contentId);
                }

            } else {
                res.sendRedirect("index.html");
            }
            session.close();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
