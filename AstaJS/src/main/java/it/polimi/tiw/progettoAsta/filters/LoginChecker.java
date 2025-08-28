package it.polimi.tiw.progettoAsta.filters;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import it.polimi.tiw.progettoAsta.bean.SessionUser;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class LoginChecker
 */
public class LoginChecker implements Filter {

	/**
	 * Default constructor.
	 */
	public LoginChecker() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginpath = "/AstaJS/";

		HttpSession s = req.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().println(loginpath);
			return;
		}
		else {
			boolean status = false;
			Cookie[] cookies = req.getCookies();
			Gson gson = new Gson();
			String username = ((SessionUser)s.getAttribute("user")).getUsername();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().contains("userData")) {
						// cerco la cookie relativo al utente
						String decodedJson = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
						Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
						Map<String, Object> parsedJson = gson.fromJson(decodedJson, mapType);
						String cookieUserName = (String) parsedJson.get("username");
						if (username.equals(cookieUserName)) {
							status = true;
							break;
						}
					}
				}
			}
			if (status == false) {
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				res.getWriter().println(loginpath);
				return;
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
