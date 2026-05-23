package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import static model.Util.isEmailValid;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "SignIn", urlPatterns = {"/SignIn"})
public class SignIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().setAttribute("forgotPasswordVerify", "notVerified");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        String email = signIn.get("email").getAsString();
        String password = signIn.get("password").getAsString();
        boolean rememberMe = signIn.get("rememberMe").getAsBoolean();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email address can not be empty!");
        } else if (!isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid email address!");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password can not be empty!");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            try {

                Criteria c1 = session.createCriteria(User.class);
                c1.add(Restrictions.eq("email", email));
                c1.add(Restrictions.eq("password", password));

                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message", "Incorrect email address or password!");
                } else {
                    User u = (User) c1.list().get(0);
                    HttpSession ses = request.getSession();

                    if (u.getUser_status() == null) {
                        responseObject.addProperty("message", "Account status error. Please contact admin.");
                    }

                    if (!u.getUser_status().getName().equalsIgnoreCase("Verified")) {   // not verified
                        ses.setAttribute("email", email);
                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "1");
                    } else {                                         // verified
                        ses.setAttribute("user", u);

                        if (rememberMe) {
                            ses.setMaxInactiveInterval(172800);   // 2 days
                        }

                        responseObject.addProperty("status", true);
                        responseObject.addProperty("message", "Login successful!");
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                responseObject.addProperty("message", "Internal server error occurred! Please try again.");
            } finally {
                if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

}
