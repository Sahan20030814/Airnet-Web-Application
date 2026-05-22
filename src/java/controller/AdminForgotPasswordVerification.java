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
@WebServlet(name = "AdminForgotPasswordVerification", urlPatterns = {"/AdminForgotPasswordVerification"})
public class AdminForgotPasswordVerification extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject signIn = gson.fromJson(request.getReader(), JsonObject.class);

        final String email = signIn.get("email").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        request.getSession().setAttribute("enableAdminForgotPassword", "disabled");

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email address can not be empty!");
        } else if (!isEmailValid(email)) {
            responseObject.addProperty("message", "Invalid email address!");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            Criteria c1 = session.createCriteria(Admin.class);
            c1.add(Restrictions.eq("email", email));

            if (c1.list().isEmpty()) {
                responseObject.addProperty("message", "Incorrect email address!");
            } else {
                Admin a = (Admin) c1.list().get(0);

                final String verification_code = Util.generateCode();
                a.setVerification(verification_code);

                session.update(a);
                session.beginTransaction().commit();
                // hibernate update

                // send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "AIRNET - Admin Account Verification", "<h1>Admin account verification code: <span style='color:red;'>" + verification_code + "</span></h1>");
                    }
                }).start();
                // send email

                // session management
                request.getSession().setAttribute("adminEmail", email);
                request.getSession().setAttribute("enableAdminForgotPassword", "enabled");
                // session management

                responseObject.addProperty("status", true);
            }
            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
