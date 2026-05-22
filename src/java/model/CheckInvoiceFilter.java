package model;

import hibernate.Checkout;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
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

@WebFilter(urlPatterns = {"/invoice.html"})
public class CheckInvoiceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        String invoiceId = req.getParameter("invoiceId");

        if (invoiceId == null || invoiceId.isEmpty() || !isInteger(invoiceId)) {
            res.sendRedirect("index.html");
        } else {
            HttpSession ses = req.getSession(false);
            if (ses != null && ses.getAttribute("user") != null) {

                User user = (User) ses.getAttribute("user");
                SessionFactory sf = HibernateUtil.getSessionFactory();
                Session session = sf.openSession();

                User verifyUser = (User) session.get(User.class, user.getId());
                if (verifyUser != null) {

                    Criteria c1 = session.createCriteria(Checkout.class);
                    c1.add(Restrictions.eq("id", Integer.parseInt(invoiceId)));
                    c1.add(Restrictions.eq("user", verifyUser));
                    Checkout checkout = (Checkout) c1.uniqueResult();

                    if (checkout != null) {
                        chain.doFilter(request, response);
                    } else {
                        res.sendRedirect("index.html");
                    }
                } else {
                    res.sendRedirect("index.html");
                }
                session.close();

            } else {
                res.sendRedirect("index.html");
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

}
