package it.polimi.tiw.progettoAsta.controller;

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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;
import it.polimi.tiw.progettoAsta.dao.OfferDAO;

/**
 * Servlet implementation class EndAuction
 */
@WebServlet("/EndAuction")
public class EndAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EndAuction() {
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
		String id = (String) session.getAttribute("idDettaglio");
		session.removeAttribute("idDettaglio");
		if (id == null || id.trim().isEmpty()) {
			response.sendRedirect("/AstaHTML/Vendo");
			return;
		}
		Integer id_asta = Integer.parseInt(id);
		AuctionDAO auctionDao = new AuctionDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		Timestamp now = Timestamp.from((Instant.now().atZone(ZoneId.of("Europe/Rome"))).toInstant().truncatedTo(ChronoUnit.MINUTES));
		AuctionBean auction = null;
		OfferBean offer = null;
		try {
			auction = auctionDao.getAuctionById(id_asta);
			if (auction != null && now.after(auction.getData_scadenza())) {
				offer = offerDao.findMaxOffer(id_asta);
				if (offer != null) {
					auctionDao.endAuction(id_asta, offer.getUser());
				}
				else {
					auctionDao.endAuction(id_asta, null);
				}
			}
		}
		catch (SQLException e) {
			session.setAttribute("idDettaglio", id);
			return;
		}
		response.sendRedirect("/AstaHTML/Vendo");
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
