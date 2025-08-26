package it.polimi.tiw.progettoAsta.controller;

import jakarta.servlet.RequestDispatcher;
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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;
import it.polimi.tiw.progettoAsta.bean.AuctionBean;
import it.polimi.tiw.progettoAsta.bean.OfferBean;
import it.polimi.tiw.progettoAsta.dao.ArticleDAO;
import it.polimi.tiw.progettoAsta.dao.AuctionDAO;
import it.polimi.tiw.progettoAsta.dao.OfferDAO;

/**
 * Servlet implementation class ShowVendo
 */
@WebServlet("/Vendo")
public class ShowVendo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ShowVendo() {
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
		String username = (String) session.getAttribute("username");
		AuctionDAO auctionDao = new AuctionDAO(connection);
		ArticleDAO articleDao = new ArticleDAO(connection);
		OfferDAO offerDao = new OfferDAO(connection);
		List<AuctionBean> auctionList = null;
		try {
			auctionList = auctionDao.findAuctionByCreator(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		List<AuctionBean> openAuction = new ArrayList<>();
		List<AuctionBean> closedAuction = new ArrayList<>();
		Map<Integer, List<ArticleBean>> articleMap = new HashMap<>();
		Map<Integer, BigDecimal> offerMap = new HashMap<>();
		Map<Integer, String> remainingTime = new HashMap<>();
		List<ArticleBean> freeArticle = null;
		Instant loginTime = ((Timestamp) session.getAttribute("login_timestamp")).toInstant();
		if (auctionList != null) {
			for (AuctionBean auction : auctionList) {
				int id_asta = auction.getId_asta();
				try {
					articleMap.put(id_asta, articleDao.findArticleByAuction(id_asta));
					OfferBean maxOffer = offerDao.findMaxOffer(id_asta);
					offerMap.put(id_asta, (maxOffer == null ? null : maxOffer.getP_offerta()));
					Instant endTime = auction.getData_scadenza().toInstant();
					Duration diff = Duration.between(loginTime, endTime);
					long remainingDays = diff.toDays();
					int remainingHours = diff.toHoursPart();
					remainingTime.put(id_asta, remainingDays + "gg " + remainingHours + "h");
					if (auction.isStatus()) {
						closedAuction.addLast(auction);
					}
					else {
						openAuction.addLast(auction);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			freeArticle = articleDao.findArticleByUser(username);
			if (freeArticle == null) {
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Timestamp now = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
		request.setAttribute("timeNow", now);
		request.setAttribute("openAuctionList", openAuction);
		request.setAttribute("closedAuctionList", closedAuction);
		request.setAttribute("remainingTime", remainingTime);
		request.setAttribute("articleMap", articleMap);
		request.setAttribute("offerMap", offerMap);
		request.setAttribute("freeArticle", freeArticle);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/vendoPage.jsp");
		dispatcher.forward(request, response);
	}

}
