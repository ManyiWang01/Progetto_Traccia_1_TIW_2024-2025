package it.polimi.tiw.progettoAsta.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.bean.UserBean;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;
import it.polimi.tiw.progettoAsta.dao.OfferDAO;
import it.polimi.tiw.progettoAsta.dao.UserDAO;

/**
 * Servlet implementation class ShowDettaglioAsta
 */
@WebServlet("/DettaglioAsta")
public class ShowDettaglioAsta extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowDettaglioAsta() {
        super();
    }

    public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("success_log") == null) {
			response.sendRedirect("/AstaHTML/");
			return;
			// redirect to login page
		}
		String id = request.getParameter("id");
		if (id == null || id.trim().isEmpty()) {
			response.sendRedirect("/AstaHTML/Vendo");
			return;
		}
		Integer id_asta = Integer.parseInt(id);
		AuctionDAO auctionDao = new AuctionDAO(connection);
		ArticleDAO articleDao = new ArticleDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		AuctionBean auction = null;
		List<ArticleBean> articleList = null;
		List<OfferBean> offerList = null;
		OfferBean maxOffer = null;
		UserBean winner = null;
		try {
			auction = auctionDao.getAuctionById(id_asta);
			articleList = articleDao.findArticleByAuction(id_asta);
			if (!auction.isStatus()) {
				offerList = offerDao.findOfferByAuction(id_asta);
				request.setAttribute("offerte", offerList);
			}
			else {
				UserDAO userDao = new UserDAO(connection);
				maxOffer = offerDao.findMaxOffer(id_asta);
				if (maxOffer != null) {
					winner = userDao.getUserInfo(maxOffer.getUser());
				}
				request.setAttribute("offertaMassima", maxOffer);
				request.setAttribute("winner", winner);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		request.setAttribute("asta", auction);
		request.setAttribute("articoli", articleList);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/dettaglioAsta.jsp");
		dispatcher.forward(request, response);
	}

}
