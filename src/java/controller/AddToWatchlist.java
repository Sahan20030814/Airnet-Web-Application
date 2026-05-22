package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import hibernate.HibernateUtil;
import hibernate.MainMovie;
import hibernate.Status;
import hibernate.User;
import hibernate.Watchlist;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sahan
 */
@WebServlet(name = "AddToWatchlist", urlPatterns = {"/AddToWatchlist"})
public class AddToWatchlist extends HttpServlet {

    private static final int ACTIVE_STATUS = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String contentId = request.getParameter("contentId");

        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        responseObject.addProperty("statusId", "0");

        SessionFactory sf = HibernateUtil.getSessionFactory();
        Session session = sf.openSession();
        Transaction tr = session.beginTransaction();

        try {
            if (contentId != null && !contentId.isEmpty() && Util.isInteger(contentId)) {

                Status status = (Status) session.get(Status.class, AddToWatchlist.ACTIVE_STATUS);
                Criteria c1 = session.createCriteria(MainMovie.class);
                c1.add(Restrictions.eq("id", Integer.parseInt(contentId)));
                c1.add(Restrictions.eq("status", status));
                MainMovie mainContent = (MainMovie) c1.uniqueResult();

                if (mainContent == null) {
                    responseObject.addProperty("message", "Something went wrong. Please refresh the page!");
                } else {
                    User user = (User) request.getSession().getAttribute("user");
                    if (user != null) {
                        User verifyUser = (User) session.get(User.class, user.getId());
                        if (verifyUser == null) {
                            responseObject.addProperty("message", "Something went wrong. Please sign in again!");
                        } else {
                            Criteria c2 = session.createCriteria(Watchlist.class);
                            c2.add(Restrictions.eq("user", verifyUser));
                            c2.add(Restrictions.eq("mainMovie", mainContent));
                            List<Watchlist> watchlistDataList = c2.list();

                            if (watchlistDataList == null || watchlistDataList.isEmpty()) {
                                Watchlist newWatchlist = new Watchlist();
                                newWatchlist.setUser(verifyUser);
                                newWatchlist.setMainMovie(mainContent);
                                session.save(newWatchlist);

                                responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " added to watchlist successfully!");
                                responseObject.addProperty("statusId", "1");
                                responseObject.addProperty("status", true);
                            } else {

                                for (Watchlist watchlist : watchlistDataList) {
                                    Watchlist watchlistItem = (Watchlist) session.get(Watchlist.class, watchlist.getId());
                                    if (watchlistItem != null) {
                                        session.delete(watchlistItem);
                                    }
                                }
                                session.flush();
                                session.clear();

                                responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " removed from watchlist successfully!");
                                responseObject.addProperty("statusId", "2");
                                responseObject.addProperty("status", true);
                            }
                            tr.commit();
                        }
                    } else {
                        HttpSession ses = request.getSession();
                        if (ses.getAttribute("sessionWatchlist") == null) {

                            ArrayList<Watchlist> sessionWatchlist = new ArrayList();
                            Watchlist newWatchlist = new Watchlist();
                            newWatchlist.setUser(null);
                            newWatchlist.setMainMovie(mainContent);
                            sessionWatchlist.add(newWatchlist);
                            ses.setAttribute("sessionWatchlist", sessionWatchlist);

                            responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " added to watchlist successfully!");
                            responseObject.addProperty("statusId", "1");
                            responseObject.addProperty("status", true);
                        } else {

                            ArrayList<Watchlist> sessionWatchlist = (ArrayList<Watchlist>) ses.getAttribute("sessionWatchlist");
                            Watchlist foundedWatchlist = null;

                            for (Watchlist watchlist : sessionWatchlist) {
                                if (watchlist.getMainMovie().getId() == mainContent.getId()) {
                                    foundedWatchlist = watchlist;
                                    break;
                                }
                            }

                            if (foundedWatchlist == null) {
                                foundedWatchlist = new Watchlist();
                                foundedWatchlist.setUser(null);
                                foundedWatchlist.setMainMovie(mainContent);
                                sessionWatchlist.add(foundedWatchlist);
                                ses.setAttribute("sessionWatchlist", sessionWatchlist);

                                responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " added to watchlist successfully!");
                                responseObject.addProperty("statusId", "1");
                                responseObject.addProperty("status", true);
                            } else {
                                sessionWatchlist.remove(foundedWatchlist);
                                ses.setAttribute("sessionWatchlist", sessionWatchlist);

                                responseObject.addProperty("message", "\"" + mainContent.getName() + "\" " + mainContent.getMovieType().getName() + " removed from watchlist successfully!");
                                responseObject.addProperty("statusId", "2");
                                responseObject.addProperty("status", true);
                            }
                        }
                    }
                }

            } else {
                responseObject.addProperty("message", "Something went wrong. Please refresh the page!");
            }

        } catch (Exception e) {
            responseObject.addProperty("message", "Something went wrong. Please try again later!");
            tr.rollback();
        } finally {
            session.close();
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }
}
