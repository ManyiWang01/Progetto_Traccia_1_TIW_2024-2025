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

import it.polimi.tiw.progettoAsta.dao.ArticleDAO;

/**
 * Servlet implementation class AddArticle
 */
@WebServlet("/AddArticle")
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
		if (session == null || session.getAttribute("success_log") == null) {
			response.sendRedirect("/AstaHTML/");
			return;
			// redirect to login page
		}
		String nome = request.getParameter("nome_articolo");
		String descrizione = request.getParameter("descrizione");
		String immagine = request.getParameter("url_immagine");
		String prezzo = request.getParameter("prezzo");
		if (nome == null || descrizione == null || immagine == null || prezzo == null ||
				nome.trim().isEmpty() || descrizione.trim().isEmpty() || 
				immagine.trim().isEmpty() || prezzo.trim().isEmpty()) {
			if (nome != null && !nome.trim().isEmpty()) {
				session.setAttribute("nome", nome);
			}
			if (descrizione != null && !descrizione.trim().isEmpty()) {
				session.setAttribute("descrizione", descrizione);
			}
			if (immagine != null && !immagine.trim().isEmpty()) {
				session.setAttribute("immagine", immagine);
			}
			if (prezzo != null && !prezzo.trim().isEmpty()) {
				session.setAttribute("prezzo", prezzo);
			}
			session.setAttribute("addArticleError", "Tutti i campi devono essere riempiti");
		}
		else {
			ArticleDAO articleDao = new ArticleDAO(connection);
			try {
				articleDao.addArticle(nome, descrizione, immagine, new BigDecimal(prezzo), (String) session.getAttribute("username"));
			}
			catch (SQLException e) {
				e.printStackTrace();
				session.setAttribute("addArticleError", "Errore lato server");
				response.sendRedirect("/AstaHTML/Vendo");
				return;
			}
		}
		response.sendRedirect("/AstaHTML/Vendo");
	}
}
