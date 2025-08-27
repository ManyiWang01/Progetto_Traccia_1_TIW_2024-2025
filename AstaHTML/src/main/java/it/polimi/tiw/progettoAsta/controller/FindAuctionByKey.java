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
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;

/**
 * Servlet implementation class FindAuctionByKey
 */
@WebServlet("/FindAuctionByKey")
public class FindAuctionByKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FindAuctionByKey() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("success_log") == null) {
			response.sendRedirect("/AstaHTML/");
			return;
			// redirect to login page
		}
		String key = request.getParameter("searchBar");
		if (key == null || key.trim().isEmpty()) {
			session.setAttribute("searchError", "Inserisci una parola chiave da ricercare");
			response.sendRedirect("/AstaHTML/Acquisto");
			return;
		}
		String username = (String) session.getAttribute("username");
		AuctionDAO auctionDao = new AuctionDAO(connection);
		List<AuctionBean> auctionList = null;
		try {
			auctionList = auctionDao.findAuctionByKey(key, username);
			if (auctionList == null) {
				session.setAttribute("searchError", "Error lato server");
				response.sendRedirect("/AstaHTML/Acquisto");
				return;
			}
			else if (auctionList.isEmpty()) {
				session.setAttribute("emptyError", "Nessun'asta trovata con parola chiave " + request.getParameter("searchBar"));
			}
			else {
				session.setAttribute("openAuctionList", auctionList);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			session.setAttribute("searchError", "Error lato server");
			response.sendRedirect("/AstaHTML/Acquisto");
			return;
		}
		response.sendRedirect("/AstaHTML/Acquisto");
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
