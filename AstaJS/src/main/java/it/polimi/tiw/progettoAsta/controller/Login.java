package it.polimi.tiw.progettoAsta.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.bean.SessionUser;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;
import it.polimi.tiw.progettoAsta.dao.OfferDAO;
import it.polimi.tiw.progettoAsta.dao.UserDAO;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
@MultipartConfig
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
		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Nome utente e password sono obbligatori");
	        return;
		}
		UserDAO userDao = new UserDAO(connection);
		try {
			if (!userDao.checkUserPassword(username, password)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("Nome utente o password errati");
		        return;
			}
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, retry later");
			return;
		}
		HttpSession session = request.getSession(true);
		SessionUser sessionUser = new SessionUser();
		Timestamp now = Timestamp.from((Instant.now().atZone(ZoneId.of("Europe/Rome"))).toInstant().truncatedTo(ChronoUnit.MINUTES));
		sessionUser.setUsername(username);
		sessionUser.setLoginTime(now);
		session.setAttribute("user", sessionUser);
		Map<String, Object> datiNeccessari = new HashMap<>();
		Gson gson = new Gson();
		Cookie[] cookies = request.getCookies();
		Map<String, Object> datiUser = new HashMap<>();
		datiUser.put("username", username);
		datiUser.put("lastAction", "new");
		datiUser.put("lastVisited", new ArrayList<Integer>());
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains("userData")) {
					// cerco la cookie relativo al utente
					String decodedJson = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
					Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
					Map<String, Object> parsedJson = gson.fromJson(decodedJson, mapType);
					String cookieUserName = (String) parsedJson.get("username");
					if (username.equals(cookieUserName)) {
						String json = gson.toJson(datiUser);
						String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString());
						Cookie newCookie = new Cookie("userData_" + username, encodedJson);
						newCookie.setPath("/AstaJS/");
						newCookie.setMaxAge(30 * 24 * 60 * 60);
						response.addCookie(newCookie);
						response.setStatus(HttpServletResponse.SC_OK);
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						response.getWriter().write(gson.toJson(parsedJson));
						return;
					}
				}
			}
		}
		String json = gson.toJson(datiUser);
		String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString());
		Cookie newCookie = new Cookie("userData_" + username, encodedJson);
		newCookie.setPath("/AstaJS/");
		newCookie.setMaxAge(30 * 24 * 60 * 60);
		response.addCookie(newCookie);
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
