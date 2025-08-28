package it.polimi.tiw.progettoAsta.controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;
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

import it.polimi.tiw.progettoAsta.bean.SessionUser;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;

/**
 * Servlet implementation class AddArticle
 */
@WebServlet("/AddArticle")
@MultipartConfig
public class AddArticle extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddArticle() {
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
		String nome = request.getParameter("nome_articolo");
		String descrizione = request.getParameter("descrizione");
		String immagine = request.getParameter("url_immagine");
		String prezzo = request.getParameter("prezzo");
		BigDecimal price = null;
		try {				
			price = new BigDecimal(prezzo);
		}
		catch (NumberFormatException n) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Campo prezzo format error");
			return;
		}
		if (nome == null || descrizione == null || immagine == null || prezzo == null ||
				nome.trim().isEmpty() || descrizione.trim().isEmpty() || 
				immagine.trim().isEmpty() || prezzo.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Tutti i campi devono essere riempiti");
			return;
		}
		else {
			ArticleDAO articleDao = new ArticleDAO(connection);
			try {
				articleDao.addArticle(nome, descrizione, immagine, price, ((SessionUser) session.getAttribute("user")).getUsername());
			}
			catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error lato server");
				return;
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
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
