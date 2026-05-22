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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@MultipartConfig
@WebServlet(name = "RegisterEpisodeData", urlPatterns = {"/RegisterEpisodeData"})
public class RegisterEpisodeData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String typeId = request.getParameter("typeId");
        String contentId = request.getParameter("contentId");
        String episodeName = request.getParameter("episodeName");
        Part episode = request.getPart("episode");

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please sign in first!");
        } else if (!Util.isInteger(typeId)) {
            responseObject.addProperty("message", "Invalid content type!");
        } else if (typeId.equals("0")) {
            responseObject.addProperty("message", "Please select the main content type!");
        } else if (!Util.isInteger(contentId)) {
            responseObject.addProperty("message", "Selected main content is not a valid content!");
        } else if (contentId.equals("0")) {
            responseObject.addProperty("message", "Please select the main content first!");
        } else if (episodeName.isEmpty()) {
            responseObject.addProperty("message", "Episode name can not be empty!");
        } else if (episode.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Please select the uploading video file!");
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
                        c3.add(Restrictions.eq("name", episodeName));
                        c3.add(Restrictions.eq("mainMovie", mainMovie));
                        Episodes oldEpisode = (Episodes) c3.uniqueResult();

                        if (oldEpisode != null) {
                            responseObject.addProperty("message", "Entered episode name has already been taken!");
                        } else {

                            Episodes updatingEpisode = new Episodes();
                            updatingEpisode.setName(episodeName);
                            updatingEpisode.setMainMovie(mainMovie);
                            updatingEpisode.setRegistered_at(new Date());

                            int episode_id = (int) session.save(updatingEpisode);
                            session.beginTransaction().commit();

                            String app_path = getServletContext().getRealPath("");
                            String new_path = app_path.replace("build" + File.separator + "web", "web" + File.separator + "product_images");

                            File productFolder = new File(new_path, contentId);
                            if (!productFolder.exists()) {
                                productFolder.mkdirs(); // make sure all directories are created
                            }

                            File file = new File(productFolder, contentId + "_" + episode_id + ".mp4");
                            Files.copy(episode.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            responseObject.addProperty("message", "New episode uploaded successfully!");
                            responseObject.addProperty("status", true);
                        }
                    }

                }
            }

            session.close();
        }

        Gson gson = new Gson();
        String json = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
