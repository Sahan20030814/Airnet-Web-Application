package model;

import hibernate.Checkout;
import hibernate.HibernateUtil;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@WebFilter(urlPatterns = {"/admin_invoice.html"})
public class CheckAdminInvoiceFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        String invoiceId = req.getParameter("invoiceId");

        if (invoiceId == null || invoiceId.isEmpty() || !isInteger(invoiceId)) {
            res.sendRedirect("admin_panel.html");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Checkout checkout = (Checkout) session.get(Checkout.class, Integer.parseInt(invoiceId));

            if (checkout != null) {
                HttpSession ses = req.getSession(false);
                if (ses != null && ses.getAttribute("admin") != null) {
                    chain.doFilter(request, response);
                } else {
                    res.sendRedirect("admin_signin.html");
                }

            } else {
                res.sendRedirect("admin_panel.html");
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
