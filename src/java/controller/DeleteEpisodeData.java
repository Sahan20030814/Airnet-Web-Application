/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Episodes;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.MovieType;
import hibernate.User;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "DeleteEpisodeData", urlPatterns = {"/DeleteEpisodeData"})
public class DeleteEpisodeData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();
        JsonObject episodeData = gson.fromJson(request.getReader(), JsonObject.class);

        String typeId = episodeData.get("typeId").getAsString();
        String contentId = episodeData.get("contentId").getAsString();
        String episodeId = episodeData.get("episodeId").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please sign in first!");
        } else if (!Util.isInteger(typeId)) {
            responseObject.addProperty("message", "Invalid content type!");
        } else if (typeId.equals("0")) {
            responseObject.addProperty("message", "Please select the main content type first!");
        } else if (!Util.isInteger(contentId)) {
            responseObject.addProperty("message", "Selected main content is not a valid content!");
        } else if (contentId.equals("0")) {
            responseObject.addProperty("message", "Please select the main content first!");
        } else if (!Util.isInteger(episodeId)) {
            responseObject.addProperty("message", "Selected episode is not a valid episode!");
        } else if (episodeId.equals("0")) {
            responseObject.addProperty("message", "Please select the deleting episode!");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            User user = (User) request.getSession().getAttribute("user");

            Criteria c1 = session.createCriteria(User.class);
            c1.add(Restrictions.eq("email", user.getEmail()));
            User u1 = (User) c1.uniqueResult();

            if (u1 == null) {
                responseObject.addProperty("message", "Something went wrong. Please sign in again!");
            } else {

                MovieType movieType = (MovieType) session.get(MovieType.class, Integer.parseInt(typeId));

                if (movieType == null) {
                    responseObject.addProperty("message", "Invalid content type!");
                } else {

                    Criteria c2 = session.createCriteria(MainMovie.class);
                    c2.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                    c2.add(Restrictions.eq("movieType", movieType));
                    c2.add(Restrictions.eq("user", u1));
                    MainMovie mainMovie = (MainMovie) c2.uniqueResult();

                    if (mainMovie == null) {
                        responseObject.addProperty("message", "Something went wrong. Please reload the page!");
                    } else {

                        Criteria c3 = session.createCriteria(Episodes.class);
                        c3.add(Restrictions.eq("id", Integer.parseInt(episodeId)));
                        c3.add(Restrictions.eq("mainMovie", mainMovie));
                        Episodes deletingEpisode = (Episodes) c3.uniqueResult();

                        if (deletingEpisode == null) {
                            responseObject.addProperty("message", "Selected episode is not in our database. Please reload the page!");
                        } else {

                            session.delete(deletingEpisode);
                            session.beginTransaction().commit();

                            String app_path = getServletContext().getRealPath("");
                            String new_path = app_path.replace("build" + File.separator + "web", "web" + File.separator + "product_images");

                            File episodeFile = new File(new_path + File.separator + contentId + File.separator + contentId + "_" + episodeId + ".mp4");
                            if (episodeFile.exists()) {
                                episodeFile.delete();
                            }

                            responseObject.addProperty("message", "Selected episode deleted successfully!");
                            responseObject.addProperty("status", true);
                        }
                    }

                }
            }

            session.close();
        }

        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
