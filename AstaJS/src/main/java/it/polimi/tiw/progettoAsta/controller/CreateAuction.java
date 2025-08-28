package it.polimi.tiw.progettoAsta.controller;

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
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.tiw.progettoAsta.bean.SessionUser;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;

/**
 * Servlet implementation class CreateAuction
 */
@WebServlet("/CreateAuction")
@MultipartConfig
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
		Integer rialzoMinimo = null;
		try {
			rialzoMinimo = Integer.parseInt(request.getParameter("rialzo_minimo"));
		}
		catch (NumberFormatException n) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Il rialzo minimo deve essere un numero intero positivo");
			return;
		}
		StringBuilder timestamp = new StringBuilder(request.getParameter("date").replace("T", " ")).append(":00");
		Timestamp scadenza = Timestamp.valueOf(timestamp.toString());
		Timestamp now = Timestamp.from((Instant.now().atZone(ZoneId.of("Europe/Rome"))).toInstant().truncatedTo(ChronoUnit.MINUTES));
		String[] articoloIDArray = request.getParameterValues("selectedElements[]");
		if (rialzoMinimo == null || rialzoMinimo < 0 || scadenza == null || scadenza.before(now) || 
				articoloIDArray == null || articoloIDArray.length == 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Tutti i campi devono essere riempiti");
			return;
		}
		else {
			AuctionDAO auctionDao = new AuctionDAO(connection);
			ArticleDAO articleDao = new ArticleDAO(connection);
			List<Integer> articoloList = new ArrayList<>();
			try {
				for (String id : articoloIDArray) {
					articoloList.add(Integer.parseInt(id));
				}
				int lastAuction = auctionDao.createAuction(articleDao.computeInitialPrice(articoloList), rialzoMinimo, scadenza.toString(), ((SessionUser) session.getAttribute("user")).getUsername());
				if (lastAuction == -1) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					response.getWriter().println("Error lato server");
					return;
				}
				articleDao.updateArticleStatus(lastAuction, articoloList);
			}
			catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error lato server");
				return;
			}
		}
		Gson gson = new Gson();
		Cookie[] cookies = request.getCookies();
		String username = ((SessionUser) session.getAttribute("user")).getUsername();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains("userData")) {
					String decodedJson = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
					Map<String, Object> userData = gson.fromJson(decodedJson, new TypeToken<Map<String, Object>>(){}.getType());
					String cookieUserName = (String) userData.get("username");
					if (username.equals(cookieUserName)) {
						userData.put("lastAction", "Vendo");
						String json = gson.toJson(userData);
						String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString());
						Cookie newCookie = new Cookie("userData_" + username, encodedJson);
						newCookie.setPath("/AstaJS/");
						newCookie.setMaxAge(30 * 24 * 60 * 60);
						response.addCookie(newCookie);
						break;
					}
				}
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
