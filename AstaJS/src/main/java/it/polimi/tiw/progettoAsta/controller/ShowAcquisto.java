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

import com.google.gson.Gson;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.bean.SessionUser;
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
		AuctionDAO auctionDao = new AuctionDAO(connection);
		ArticleDAO articleDao = new ArticleDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		List<AuctionBean> winnedAuctionList = null;
		Map<Integer, List<ArticleBean>> winnedArticleMap = new HashMap<>();
		Map<Integer, BigDecimal> maxOfferMap = new HashMap<>();
		Map<String, Object> datiNeccessari = new HashMap<>();
		Gson gson = new Gson();
		try {
			winnedAuctionList = auctionDao.findAuctionByWinner(((SessionUser) session.getAttribute("user")).getUsername());
			if (winnedAuctionList.isEmpty()) {
				String json = gson.toJson(datiNeccessari);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.getWriter().write(json);
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
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error lato server");
			return;
		}
		datiNeccessari.put("maxOffer", maxOfferMap);
		datiNeccessari.put("winnedAuction", winnedAuctionList);
		datiNeccessari.put("winnedArticle", winnedArticleMap);
		
		String json = gson.toJson(datiNeccessari);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.getWriter().write(json);
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
