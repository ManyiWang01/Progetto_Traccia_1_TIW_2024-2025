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
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;
import it.polimi.tiw.progettoAsta.dao.OfferDAO;

/**
 * Servlet implementation class ShowOfferta
 */
@WebServlet("/Offerta")
public class ShowOfferta extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowOfferta() {
        super();
        // TODO Auto-generated constructor stub
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
			response.sendRedirect("/AstaHTML/Acquisto");
			return;
		}
		Integer id_asta = Integer.parseInt(id);
		AuctionDAO auctionDao = new AuctionDAO(connection);
		ArticleDAO articleDao = new ArticleDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		List<OfferBean> offerList = null;
		List<ArticleBean> articleList = null;
		AuctionBean auction = null;
		OfferBean maxOffer = null;
		try {
			auction = auctionDao.getAuctionById(id_asta);
			offerList = offerDao.findOfferByAuction(id_asta);
			articleList = articleDao.findArticleByAuction(id_asta);
			maxOffer = offerDao.findMaxOffer(id_asta);
			if (offerList == null || articleList == null || auction == null) {
				response.sendRedirect("/AstaHTML/Acquisto");
				return;
			}
			if (offerList.isEmpty()) {
				request.setAttribute("emptyOffer", "Al momento non ci sono offerte");
			}
			if (articleList.isEmpty()) {
				request.setAttribute("emptyArticle", "Error: Non ci sono articoli trovati in questa asta");
			}
		}
		catch (SQLException e) {
			request.setAttribute("offerError", "Error lato server");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/offertaAsta.jsp");
			dispatcher.forward(request, response);
			return;
		}
		request.setAttribute("auction", auction);
		request.setAttribute("offerList", offerList);
		request.setAttribute("articleList", articleList);
		request.setAttribute("maxOffer", maxOffer);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/offertaAsta.jsp");
		dispatcher.forward(request, response);
	}
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
		}
	}

}
