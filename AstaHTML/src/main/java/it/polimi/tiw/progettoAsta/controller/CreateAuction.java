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
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/CreateAuction")
public class CreateAuction extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateAuction() {
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
		Integer rialzoMinimo = Integer.parseInt(request.getParameter("rialzo_minimo"));
		StringBuilder timestamp = new StringBuilder(request.getParameter("date").replace("T", " ")).append(":00");
		Timestamp scadenza = Timestamp.valueOf(timestamp.toString());
		Timestamp now = Timestamp.from((Instant.now().atZone(ZoneId.of("Europe/Rome"))).toInstant().truncatedTo(ChronoUnit.MINUTES));
		String[] articoloIDArray = request.getParameterValues("selectedElements[]");
		if (rialzoMinimo == null || rialzoMinimo < 0 || scadenza == null || scadenza.before(now) || 
				articoloIDArray == null || articoloIDArray.length == 0) {
			if (!(rialzoMinimo == null || rialzoMinimo < 0)) {
				session.setAttribute("rialzoMinimo", rialzoMinimo);
			}
			if (!(scadenza == null || scadenza.before(now))) {
				session.setAttribute("date", scadenza);
			}
			session.setAttribute("astaError", "Tutti i campi devono essere riempiti!");
		}
		else {
			AuctionDAO auctionDao = new AuctionDAO(connection);
			ArticleDAO articleDao = new ArticleDAO(connection);
			List<Integer> articoloList = new ArrayList<>();
			try {
				for (String id : articoloIDArray) {
					articoloList.add(Integer.parseInt(id));
				}
				int lastAuction = auctionDao.createAuction(articleDao.computeInitialPrice(articoloList), rialzoMinimo, scadenza.toString(), (String) session.getAttribute("username"));
				articleDao.updateArticleStatus(lastAuction, articoloList);
			}
			catch (SQLException e) {
				e.printStackTrace();
				session.setAttribute("astaError", "Error lato server");
				response.sendRedirect("/AstaHTML/Vendo");
				return;
			}
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
