package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Admin;
import hibernate.HibernateUtil;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.Util;
import static model.Util.isEmailValid;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AdminSignIn", urlPatterns = {"/AdminSignIn"})
public class AdminSignIn extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().setAttribute("forgotPasswordVerify", "notVerified");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        final String email = signIn.get("email").getAsString();
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

            Criteria c1 = session.createCriteria(Admin.class);
            c1.add(Restrictions.eq("email", email));
            c1.add(Restrictions.eq("password", password));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "Incorrect email address or password!");
            } else {
                Admin a = (Admin) c1.list().get(0);

                final String verification_code = Util.generateCode();
                a.setVerification(verification_code);

                session.update(a);
                session.beginTransaction().commit();

                // send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "AIRNET - Admin Verification", "<h1>Admin verification code: <span style='color:red;'>" + verification_code + "</span></h1>");
                    }
                }).start();
                // send email

                HttpSession ses = request.getSession();
                ses.setAttribute("adminEmail", email);
                if (rememberMe) {
                    ses.setAttribute("adminRememberMe", "true");
                } else {
                    ses.setAttribute("adminRememberMe", "false");
                }

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
