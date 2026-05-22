/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.Country;
import hibernate.Genre;
import hibernate.HibernateUtil;
import hibernate.Language;
import hibernate.MainMovie;
import hibernate.MainMovieHasCountry;
import hibernate.MainMovieHasGenre;
import hibernate.MainMovieHasLanguage;
import hibernate.MovieType;
import hibernate.QualityType;
import hibernate.Status;
import hibernate.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
import static model.Util.generateUniqueId;
import static model.Util.getConvertedLink;
import static model.Util.getReleaseDateObject;
import static model.Util.isValidDate;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@MultipartConfig
@WebServlet(name = "RegisterContentData", urlPatterns = {"/RegisterContentData"})
public class RegisterContentData extends HttpServlet {

    private static final int ACTIVE_STATUS_ID = 1;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String typeId = request.getParameter("typeId");
        String qualityId = request.getParameter("qualityId");
        String price = request.getParameter("price");
        String genreCollection = request.getParameter("genreCollection");
        String countryCollection = request.getParameter("countryCollection");
        String languageCollection = request.getParameter("languageCollection");
        String releasedAt = request.getParameter("releasedAt");
        String episodeCount = request.getParameter("episodeCount");
        String duration = request.getParameter("duration");
        String cast = request.getParameter("cast");
        String production = request.getParameter("production");
        String trailer = request.getParameter("trailer");
        Part bgImg = request.getPart("bgImg");
        Part cardImg = request.getPart("cardImg");

        ArrayList<Integer> genreIds = new ArrayList<>();

        if (genreCollection != null && !genreCollection.trim().isEmpty()) {
            String[] parts = genreCollection.split(",");
            for (String part : parts) {
                try {
                    genreIds.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                }
            }
        }

        ArrayList<Integer> countryIds = new ArrayList<>();

        if (countryCollection != null && !countryCollection.trim().isEmpty()) {
            String[] parts = countryCollection.split(",");
            for (String part : parts) {
                try {
                    countryIds.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                }
            }
        }

        ArrayList<Integer> languageIds = new ArrayList<>();

        if (languageCollection != null && !languageCollection.trim().isEmpty()) {
            String[] parts = languageCollection.split(",");
            for (String part : parts) {
                try {
                    languageIds.add(Integer.parseInt(part.trim()));
                } catch (NumberFormatException e) {
                }
            }
        }

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please sign in first!");
        } else if (name.isEmpty()) {
            responseObject.addProperty("message", "Content name can not be empty!");
        } else if (description.isEmpty()) {
            responseObject.addProperty("message", "Content description can not be empty!");
        } else if (!Util.isInteger(typeId)) {
            responseObject.addProperty("message", "Invalid content type!");
        } else if (typeId.equals("0")) {
            responseObject.addProperty("message", "Please select the content type!");
        } else if (!Util.isInteger(qualityId)) {
            responseObject.addProperty("message", "Invalid video quality!");
        } else if (qualityId.equals("0")) {
            responseObject.addProperty("message", "Please select the content video quality!");
        } else if (price.isEmpty()) {
            responseObject.addProperty("message", "Content price can not be empty!");
        } else if (!Util.isDouble(price)) {
            responseObject.addProperty("message", "Invalid content price!");
        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Content price must be greater than 0!");
        } else if (genreIds.size() <= 0) {
            responseObject.addProperty("message", "Please select the genre(s) of the content!");
        } else if (countryIds.size() <= 0) {
            responseObject.addProperty("message", "Please select the country(s) of the content!");
        } else if (languageIds.size() <= 0) {
            responseObject.addProperty("message", "Please select the language(s) of the content!");
        } else if (releasedAt.isEmpty()) {
            responseObject.addProperty("message", "Content released date can not be empty!");
        } else if (!isValidDate(releasedAt)) {
            responseObject.addProperty("message", "Invalid released date!");
        } else if (episodeCount.isEmpty()) {
            responseObject.addProperty("message", "Content episode count can not be empty!");
        } else if (!Util.isInteger(episodeCount)) {
            responseObject.addProperty("message", "Invalid episode count!");
        } else if (Integer.parseInt(episodeCount) <= 0) {
            responseObject.addProperty("message", "Content must have at least one episode!");
        } else if (duration.isEmpty()) {
            responseObject.addProperty("message", "Content episode duration can not be empty!");
        } else if (!Util.isInteger(duration)) {
            responseObject.addProperty("message", "Invalid episode duration! Duration must be in minutes. Ex: 120");
        } else if (Integer.parseInt(duration) <= 0) {
            responseObject.addProperty("message", "An episode must have at least one minute duration!");
        } else if (cast.isEmpty()) {
            responseObject.addProperty("message", "Content cast can not be empty!");
        } else if (production.isEmpty()) {
            responseObject.addProperty("message", "Content production can not be empty!");
        } else if (trailer.isEmpty()) {
            responseObject.addProperty("message", "Content youtube trailer link is required!");
        } else if (bgImg.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Content background image is required!");
        } else if (cardImg.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Content card image is required!");
        } else {

            SessionFactory sf = HibernateUtil.getSessionFactory();
            Session session = sf.openSession();

            MovieType movieType = (MovieType) session.get(MovieType.class, Integer.parseInt(typeId));

            if (movieType == null) {
                responseObject.addProperty("message", "Invalid content type!");
            } else {
                QualityType qualityType = (QualityType) session.get(QualityType.class, Integer.parseInt(qualityId));

                if (qualityType == null) {
                    responseObject.addProperty("message", "Invalid video quality!");
                } else {

                    boolean isErrorDetect = false;
                    ArrayList<Genre> genreList = new ArrayList();

                    for (Integer genreId : genreIds) {
                        Genre genre = (Genre) session.get(Genre.class, genreId);
                        if (genre == null) {
                            isErrorDetect = true;
                            responseObject.addProperty("message", "One of selected genres is invalid!");
                            break;
                        } else {
                            genreList.add(genre);
                        }
                    }

                    if (!isErrorDetect) {

                        ArrayList<Country> countryList = new ArrayList();

                        for (Integer countryId : countryIds) {
                            Country country = (Country) session.get(Country.class, countryId);
                            if (country == null) {
                                isErrorDetect = true;
                                responseObject.addProperty("message", "One of selected countries is invalid!");
                                break;
                            } else {
                                countryList.add(country);
                            }
                        }

                        if (!isErrorDetect) {

                            ArrayList<Language> languageList = new ArrayList();

                            for (Integer languageId : languageIds) {
                                Language language = (Language) session.get(Language.class, languageId);
                                if (language == null) {
                                    isErrorDetect = true;
                                    responseObject.addProperty("message", "One of selected languages is invalid!");
                                    break;
                                } else {
                                    languageList.add(language);
                                }
                            }

                            if (!isErrorDetect) {
                                User user = (User) request.getSession().getAttribute("user");

                                Criteria c1 = session.createCriteria(User.class);
                                c1.add(Restrictions.eq("email", user.getEmail()));
                                User u1 = (User) c1.uniqueResult();

                                if (u1 == null) {
                                    responseObject.addProperty("message", "Something went wrong. Please sign in again!");
                                } else {

                                    Date released_date = getReleaseDateObject(releasedAt);

                                    if (released_date == null) {
                                        responseObject.addProperty("message", "Invalid released date!");
                                    } else {

                                        int uniqueId;
                                        do {
                                            uniqueId = generateUniqueId();
                                        } while ((MainMovie) session.get(MainMovie.class, uniqueId) != null);

                                        MainMovie content = new MainMovie();
                                        content.setId(uniqueId);
                                        content.setName(name);
                                        content.setDescription(description);
                                        content.setPrice(Double.parseDouble(price));
                                        content.setProduction(production);
                                        content.setCast(cast);
                                        content.setReleased_at(released_date);
                                        content.setTrailer(getConvertedLink(trailer));
                                        content.setQualityType(qualityType);
                                        content.setMovieType(movieType);
                                        content.setDuration(duration);
                                        content.setEpisode_count(Integer.parseInt(episodeCount));
                                        content.setRegistered_at(new Date());

                                        Status status = (Status) session.get(Status.class, RegisterContentData.ACTIVE_STATUS_ID);
                                        content.setStatus(status);
                                        content.setRating(0);
                                        content.setSelling_count(0);
                                        content.setUser(u1);

                                        int contentId = (int) session.save(content);

                                        for (Genre genre : genreList) {
                                            MainMovieHasGenre contentHasGenre = new MainMovieHasGenre();
                                            contentHasGenre.setMainMovie(content);
                                            contentHasGenre.setGenre(genre);
                                            session.save(contentHasGenre);
                                        }

                                        for (Country country : countryList) {
                                            MainMovieHasCountry contentHasCountry = new MainMovieHasCountry();
                                            contentHasCountry.setMainMovie(content);
                                            contentHasCountry.setCountry(country);
                                            session.save(contentHasCountry);
                                        }

                                        for (Language language : languageList) {
                                            MainMovieHasLanguage contentHasLanguage = new MainMovieHasLanguage();
                                            contentHasLanguage.setMainMovie(content);
                                            contentHasLanguage.setLanguage(language);
                                            session.save(contentHasLanguage);
                                        }

                                        session.beginTransaction().commit();

                                        String app_path = getServletContext().getRealPath("");
                                        String new_path = app_path.replace("build" + File.separator + "web", "web" + File.separator + "product_images");

                                        File productFolder = new File(new_path, String.valueOf(contentId));
                                        productFolder.mkdir();

                                        File file1 = new File(productFolder, "background_image.jpg");
                                        Files.copy(bgImg.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                        File file2 = new File(productFolder, "card_image.jpg");
                                        Files.copy(cardImg.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                        responseObject.addProperty("message", "New " + movieType.getName() + " registered successfully!");
                                        responseObject.addProperty("status", true);

                                    }
                                }
                            }

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
