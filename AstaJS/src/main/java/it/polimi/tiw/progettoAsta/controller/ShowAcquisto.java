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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;
import it.polimi.tiw.progettoAsta.dao.OfferDAO;

/**
 * Servlet implementation class ShowAcquisto
 */
@WebServlet("/Acquisto")
public class ShowAcquisto extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowAcquisto() {
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
		AuctionDAO auctionDao = new AuctionDAO(connection);
		ArticleDAO articleDao = new ArticleDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		List<AuctionBean> winnedAuctionList = null;
		Map<Integer, List<ArticleBean>> winnedArticleMap = new HashMap<>();
		Map<Integer, BigDecimal> maxOfferMap = new HashMap<>();
		try {
			winnedAuctionList = auctionDao.findAuctionByWinner((String) session.getAttribute("username"));
			if (winnedAuctionList == null || winnedAuctionList.isEmpty()) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/acquistoPage.jsp");
				dispatcher.forward(request, response);
				return;
			}
			for (AuctionBean auction : winnedAuctionList) {
				List<ArticleBean> winnedArticle = null;
				OfferBean maxOffer = null;
				Integer id_asta = auction.getId_asta();
				winnedArticle = articleDao.findArticleByAuction(id_asta);
				maxOffer = offerDao.findMaxOffer(id_asta);
				winnedArticleMap.put(id_asta, winnedArticle);
				maxOfferMap.put(id_asta, maxOffer.getP_offerta());
			}
		}
		catch (SQLException e) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/acquistoPage.jsp");
			dispatcher.forward(request, response);
			return;
		}
		request.setAttribute("maxOffer", maxOfferMap);
		request.setAttribute("winnedAuction", winnedAuctionList);
		request.setAttribute("winnedArticle", winnedArticleMap);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/acquistoPage.jsp");
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
