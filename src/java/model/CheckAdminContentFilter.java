package model;

import hibernate.HibernateUtil;
import hibernate.MainMovie;
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

@WebFilter(urlPatterns = {"/admin_single_product_view.html", "/admin_buying_content_view.html"})
public class CheckAdminContentFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        res.setHeader("Pragma", "no-cache");
        res.setHeader("Expires", "0");

        String contentId = req.getParameter("id");

        if (contentId == null || contentId.isEmpty() || !isInteger(contentId)) {
            res.sendRedirect("admin_panel.html");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            MainMovie mainMovie = (MainMovie) session.get(MainMovie.class, Integer.parseInt(contentId));

            if (mainMovie != null) {
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
