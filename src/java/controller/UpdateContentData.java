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
import hibernate.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.Util;
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
@WebServlet(name = "UpdateContentData", urlPatterns = {"/UpdateContentData"})
public class UpdateContentData extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String updatingId = request.getParameter("id");
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
        } else if (updatingId.isEmpty()) {
            responseObject.addProperty("message", "Please select the updating content first!");
        } else if (!Util.isInteger(updatingId)) {
            responseObject.addProperty("message", "Selected content is not a valid content!");
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

                Criteria c2 = session.createCriteria(MainMovie.class);
                c2.add(Restrictions.eq("id", Integer.parseInt(updatingId)));
                c2.add(Restrictions.eq("user", u1));
                MainMovie updatingContent = (MainMovie) c2.uniqueResult();

                if (updatingContent == null) {
                    responseObject.addProperty("message", "Selected content is not a valid content!");
                } else {

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

                                        Date released_date = getReleaseDateObject(releasedAt);

                                        if (released_date == null) {
                                            responseObject.addProperty("message", "Invalid released date!");
                                        } else {

                                            updatingContent.setName(name);
                                            updatingContent.setDescription(description);
                                            updatingContent.setPrice(Double.parseDouble(price));
                                            updatingContent.setProduction(production);
                                            updatingContent.setCast(cast);
                                            updatingContent.setReleased_at(released_date);
                                            updatingContent.setTrailer(getConvertedLink(trailer));
                                            updatingContent.setQualityType(qualityType);
                                            updatingContent.setMovieType(movieType);
                                            updatingContent.setDuration(duration);
                                            updatingContent.setEpisode_count(Integer.parseInt(episodeCount));

                                            session.update(updatingContent);

                                            Criteria c3 = session.createCriteria(MainMovieHasGenre.class);
                                            c3.add(Restrictions.eq("mainMovie", updatingContent));
                                            List<MainMovieHasGenre> oldMainMovieHasGenreList = c3.list();

                                            for (MainMovieHasGenre oldMainMovieHasGenre : oldMainMovieHasGenreList) {
                                                session.delete(oldMainMovieHasGenre);
                                            }

                                            for (Genre genre : genreList) {
                                                MainMovieHasGenre contentHasNewGenre = new MainMovieHasGenre();
                                                contentHasNewGenre.setMainMovie(updatingContent);
                                                contentHasNewGenre.setGenre(genre);
                                                session.save(contentHasNewGenre);
                                            }

                                            Criteria c4 = session.createCriteria(MainMovieHasCountry.class);
                                            c4.add(Restrictions.eq("mainMovie", updatingContent));
                                            List<MainMovieHasCountry> oldMainMovieHasCountryList = c4.list();

                                            for (MainMovieHasCountry oldMainMovieHasCountry : oldMainMovieHasCountryList) {
                                                session.delete(oldMainMovieHasCountry);
                                            }

                                            for (Country country : countryList) {
                                                MainMovieHasCountry contentHasNewCountry = new MainMovieHasCountry();
                                                contentHasNewCountry.setMainMovie(updatingContent);
                                                contentHasNewCountry.setCountry(country);
                                                session.save(contentHasNewCountry);
                                            }

                                            Criteria c5 = session.createCriteria(MainMovieHasLanguage.class);
                                            c5.add(Restrictions.eq("mainMovie", updatingContent));
                                            List<MainMovieHasLanguage> oldMainMovieHasLanguageList = c5.list();

                                            for (MainMovieHasLanguage oldMainMovieHasLanguage : oldMainMovieHasLanguageList) {
                                                session.delete(oldMainMovieHasLanguage);
                                            }

                                            for (Language language : languageList) {
                                                MainMovieHasLanguage contentHasNewLanguage = new MainMovieHasLanguage();
                                                contentHasNewLanguage.setMainMovie(updatingContent);
                                                contentHasNewLanguage.setLanguage(language);
                                                session.save(contentHasNewLanguage);
                                            }

                                            session.beginTransaction().commit();

                                            String app_path = getServletContext().getRealPath("");
                                            String new_path = app_path.replace("build" + File.separator + "web", "web" + File.separator + "product_images");

                                            File productFolder = new File(new_path, String.valueOf(updatingId));
                                            productFolder.mkdir();

                                            if (bgImg.getSubmittedFileName() != null) {
                                                File file1 = new File(productFolder, "background_image.jpg");
                                                Files.copy(bgImg.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                            } else if (cardImg.getSubmittedFileName() != null) {
                                                File file2 = new File(productFolder, "card_image.jpg");
                                                Files.copy(cardImg.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                            }

                                            responseObject.addProperty("message", "Selected content updated successfully!");
                                            responseObject.addProperty("status", true);
                                        }
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
