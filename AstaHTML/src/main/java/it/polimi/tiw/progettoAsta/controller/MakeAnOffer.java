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
import java.math.BigDecimal;
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
 * Servlet implementation class MakeAnOffer
 */
@WebServlet("/MakeAnOffer")
public class MakeAnOffer extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MakeAnOffer() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("success_log") == null) {
			response.sendRedirect("/AstaHTML/");
			return;
			// redirect to login page
		}
		String id_asta = (String) session.getAttribute("idOfferta");
		session.removeAttribute("idOfferta");
		if (id_asta == null || id_asta.trim().isEmpty()) {
			response.sendRedirect("/AstaHTML/Acquisto");
			return;
		}
		Integer id = null;
		try {
			id = Integer.parseInt(id_asta);
		}
		catch (NumberFormatException n) {
			response.sendRedirect("/AstaHTML/Acquisto");
			return;
		}
		OfferDAO offerDao = new OfferDAO(connection);
		AuctionDAO auctionDao = new AuctionDAO(connection);
		try {
			AuctionBean auction = auctionDao.getAuctionById(id);
			OfferBean maxOffer = offerDao.findMaxOffer(id);
			BigDecimal price = null;
			try {
				price = new BigDecimal(request.getParameter("prezzo"));
			}
			catch (NumberFormatException n) {
				price = null;
			}
			if (price == null || auction == null || maxOffer == null || (maxOffer.getP_offerta().add(new BigDecimal(auction.getMin_rialzo()))).compareTo(price) == 1 ) {
				session.setAttribute("offertaError", "Error lato server");
				session.setAttribute("idOfferta", id);
				response.sendRedirect("/AstaHTML/Offerta?id=" + id);
				return;
			}
			Timestamp now = Timestamp.from((Instant.now().atZone(ZoneId.of("Europe/Rome"))).toInstant().truncatedTo(ChronoUnit.MINUTES));
			offerDao.makeAnOffer(id, (String) session.getAttribute("username"), now, price);
		}
		catch (SQLException e) {
			session.setAttribute("offertaError", "Error lato server");
			session.setAttribute("idOfferta", id);
			response.sendRedirect("/AstaHTML/Offerta?id=" + id);
			return;
		}
		session.setAttribute("idOfferta", id);
		response.sendRedirect("/AstaHTML/Offerta?id=" + id);
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
