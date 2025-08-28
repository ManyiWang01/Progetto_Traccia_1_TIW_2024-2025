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
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

/**
 * Servlet implementation class ShowOfferta
 */
@WebServlet("/Offerta")
public class ShowOfferta extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowOfferta() {
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
		String id = request.getParameter("id");
		if (id == null || id.trim().isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("ID non trovata");
			return;
		}
		Integer id_asta = null;
		try {
			id_asta = Integer.parseInt(id);
		}
		catch (NumberFormatException n) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("ID non trovata");
			return;
		}
		AuctionDAO auctionDao = new AuctionDAO(connection);
		ArticleDAO articleDao = new ArticleDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		List<OfferBean> offerList = null;
		List<ArticleBean> articleList = null;
		AuctionBean auction = null;
		OfferBean maxOffer = null;
		try {
			auction = auctionDao.getAuctionById(id_asta);
			offerList = offerDao.findOfferByAuction(id_asta);
			articleList = articleDao.findArticleByAuction(id_asta);
			maxOffer = offerDao.findMaxOffer(id_asta);
			if (offerList == null || articleList == null || auction == null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error lato server");
				return;
			}
		}
		catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Error lato server");
			return;
		}
		Map<String, Object> datiNeccessari = new HashMap<>();
		datiNeccessari.put("auction", auction);
		datiNeccessari.put("offerList", offerList);
		datiNeccessari.put("articleList", articleList);
		datiNeccessari.put("maxOffer", maxOffer);
		Gson gson = new Gson();
		String json = gson.toJson(datiNeccessari);

		Cookie[] cookies = request.getCookies();
		String username = ((SessionUser) session.getAttribute("user")).getUsername();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().contains("userData")) {
					String decodedJson = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
					Map<String, Object> userData = gson.fromJson(decodedJson, new TypeToken<Map<String, Object>>(){}.getType());
					String cookieUserName = (String) userData.get("username");
					if (username.equals(cookieUserName)) {
						userData.put("lastAction", "Acquisto");
						String arrayList = gson.toJson(userData.get("lastVisited"));
				        Type listType = new TypeToken<List<Integer>>(){}.getType();				        
						List<Integer> lastVisited = gson.fromJson(arrayList, listType);
						if (!lastVisited.contains(id_asta)) {
							lastVisited.addFirst(id_asta);
						}
						userData.put("lastVisited", lastVisited);
						String dataJson = gson.toJson(userData);
						String encodedJson = URLEncoder.encode(dataJson, StandardCharsets.UTF_8.toString());
						Cookie newCookie = new Cookie("userData_" + username, encodedJson);
						newCookie.setPath("/AstaJS/");
						newCookie.setMaxAge(30 * 24 * 60 * 60);
						response.addCookie(newCookie);
						break;
					}
				}
			}
		}
		session.setAttribute("idOfferta", id);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
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
