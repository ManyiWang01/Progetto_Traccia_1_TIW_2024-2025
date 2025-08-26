package it.polimi.tiw.progettoAsta.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

import it.polimi.tiw.progettoAsta.dao.UserDAO;

/**
 * Servlet implementation class Login
 */
@WebServlet("/")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		HttpSession session = request.getSession(true);
		session.setAttribute("username", username);
		session.setAttribute("success_log", true);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		now.setNanos(0);
		session.setAttribute("login_timestamp", now);
		response.sendRedirect("Home");
//		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
//			request.setAttribute("error", "Nome utente e password sono obbligatori");
//			RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
//	        dispatcher.forward(request, response);
//	        return;
//		}
//		UserDAO userDao = new UserDAO(connection);
//		try {
//			if (!userDao.checkUserPassword(username, password)) {
//				request.setAttribute("error", "Nome utente o password errati");
//				RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
//		        dispatcher.forward(request, response);
//		        return;
//			}
//			else {
//				// salvare session id nella db
//				HttpSession session = request.getSession(true);
	//			session.setAttribute("username", username);
	//			session.setAttribute("success_log", true);
	//			response.sendRedirect("/AstaHTML/Home");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//			request.setAttribute("error", "Errore interno del server");
//	        RequestDispatcher dispatcher = request.getRequestDispatcher("/login.jsp");
//	        dispatcher.forward(request, response);
//		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/login.jsp");
        dispatcher.forward(request, response);
	}
}
