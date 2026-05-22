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

@WebFilter(urlPatterns = {"/checkout.html"})
public class CheckCheckoutFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        String contentId = req.getParameter("id");

        if (contentId == null || contentId.isEmpty() || !isInteger(contentId)) {
            res.sendRedirect("index.html");
        } else {
            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();
            MainMovie mainMovie = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));
            if (mainMovie != null) {
                if (mainMovie.getStatus().getName().equalsIgnoreCase("Active")) {

                    HttpSession ses = req.getSession(false);
                    if (ses != null && ses.getAttribute("user") != null) {
                        User user = (User) ses.getAttribute("user");
                        if (user != null) {
                            User verifiedUser = (User) session.get(User.class, user.getId());
                            if (verifiedUser != null) {

                                Criteria c1 = session.createCriteria(Checkout.class);
                                c1.add(Restrictions.eq("user", verifiedUser));
                                List<Checkout> checkoutList = c1.list();

                                boolean alreadyCheckedout = false;

                                for (Checkout checkout : checkoutList) {
                                    Criteria c2 = session.createCriteria(CheckoutItems.class);
                                    c2.add(Restrictions.eq("checkout", checkout));
                                    c2.add(Restrictions.eq("mainMovie", mainMovie));
                                    CheckoutItems checkoutItems = (CheckoutItems) c2.uniqueResult();
                                    if (checkoutItems != null) {
                                        alreadyCheckedout = true;
                                        break;
                                    }
                                }

                                if (alreadyCheckedout) {
                                    res.sendRedirect("single_product_view.html?id=" + contentId);
                                } else {
                                    chain.doFilter(request, response);
                                }

                            } else {
                                res.sendRedirect("signin.html");
                            }
                        } else {
                            res.sendRedirect("signin.html");
                        }
                    } else {
                        res.sendRedirect("signin.html");
                    }
                } else {

                    HttpSession ses = req.getSession(false);

                    if (ses != null && ses.getAttribute("user") != null) {
                        User user = (User) ses.getAttribute("user");
                        if (user != null) {
                            User verifiedUser = (User) session.get(User.class, user.getId());
                            if (verifiedUser != null) {

                                Criteria c1 = session.createCriteria(Checkout.class);
                                c1.add(Restrictions.eq("user", verifiedUser));
                                List<Checkout> checkoutList = c1.list();
                                boolean canContinue = false;

                                for (Checkout checkout : checkoutList) {
                                    Criteria c2 = session.createCriteria(CheckoutItems.class);
                                    c2.add(Restrictions.eq("checkout", checkout));
                                    c2.add(Restrictions.eq("mainMovie", mainMovie));
                                    CheckoutItems checkoutItems = (CheckoutItems) c2.uniqueResult();

                                    if (checkoutItems != null) {
                                        canContinue = true;
                                        break;
                                    }
                                }

                                if (canContinue) {
                                    res.sendRedirect("single_product_view.html?id=" + contentId);
                                } else {
                                    res.sendRedirect("index.html");
                                }
                            } else {
                                res.sendRedirect("signin.html");
                            }
                        } else {
                            res.sendRedirect("signin.html");
                        }
                    } else {
                        res.sendRedirect("signin.html");
                    }
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
