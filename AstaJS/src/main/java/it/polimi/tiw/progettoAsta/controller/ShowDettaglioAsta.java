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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.bean.SessionUser;
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
		String id = request.getParameter("id");
		if (id == null || id.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("ID non trovato");
			return;
		}
		Integer id_asta = null;
		try {
			id_asta = Integer.parseInt(id);
		}
		catch (NumberFormatException n) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("ID non trovato");
			return;
		}
		Map<String, Object> datiNeccessari = new HashMap<>();
		AuctionDAO auctionDao = new AuctionDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		AuctionBean auction = null;
		List<OfferBean> offerList = null;
		OfferBean maxOffer = null;
		UserBean winner = null;
		try {
			auction = auctionDao.getAuctionById(id_asta);
			if (auction == null || !auction.getCreator().equals(((SessionUser) session.getAttribute("user")).getUsername())) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error lato server");
				return;
			}
			if (!auction.isStatus()) {
				offerList = offerDao.findOfferByAuction(id_asta);
				datiNeccessari.put("offerte", offerList);
				datiNeccessari.put("status", "aperta");
			}
			else {
				UserDAO userDao = new UserDAO(connection);
				maxOffer = offerDao.findMaxOffer(id_asta);
				if (maxOffer != null) {
					winner = userDao.getUserInfo(maxOffer.getUser());
				}
				datiNeccessari.put("offertaMassima", maxOffer);
				datiNeccessari.put("winner", winner);
				datiNeccessari.put("status", "chiusa");
			}
		}
		catch (SQLException e) {
//			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error lato server");
			return;
		}
		datiNeccessari.put("asta", auction);
		session.setAttribute("idDettaglio", id);
		Gson gson = new Gson();
		
		String json = gson.toJson(datiNeccessari);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
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
