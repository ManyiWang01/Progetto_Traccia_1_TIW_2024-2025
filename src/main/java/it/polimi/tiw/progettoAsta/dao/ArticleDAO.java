package it.polimi.tiw.progettoAsta.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.ArticleBean;

public class ArticleDAO {
	private Connection connection;
	
	public ArticleDAO(Connection connection) {
		this.connection = connection;
	}
	
	public void addArticle(String nome_articolo, String descrizione, String url_immagine, BigDecimal prezzo, String username) throws SQLException {
		if (nome_articolo == null || nome_articolo.trim().isEmpty() || descrizione == null || descrizione.trim().isEmpty() || 
				url_immagine == null || url_immagine.trim().isEmpty() || prezzo == null || prezzo.compareTo(new BigDecimal("0")) < 0 || username == null || username.trim().isEmpty()) {
			return;
		}
		String query = "INSERT INTO asta.articolo (nome, descrizione, immagine, prezzo, username, id_asta) VALUES (?, ?, ?, ?, ?, NULL)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, nome_articolo);
			pstatement.setString(2, descrizione);
			pstatement.setString(3, url_immagine);
			pstatement.setBigDecimal(4, prezzo);
			pstatement.setString(5, username);
			pstatement.executeUpdate();
		}
		catch (SQLException e){
			throw new SQLException(e);
		}
		finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
	}

	public void updateArticleStatus(int id_auction, List<Integer> id_article_list) throws SQLException {
		if (id_auction < 0 || id_article_list == null || id_article_list.isEmpty()) {
			return;
		}
		StringBuilder queryString = new StringBuilder();
		queryString.append("UPDATE article SET id_auction = ? WHERE id_article IN (");
		for (int i = 0; i < id_article_list.size(); i++) {
			if(i > 0) {
				queryString.append(",");
			}
			queryString.append("?");
		}
		queryString.append(")");
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(queryString.toString());
			pstatement.setInt(1, id_auction);
			for (int i = 0; i < id_article_list.size(); i++) {
				pstatement.setInt(i + 2, id_article_list.get(i));
			}
			pstatement.executeUpdate();
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			}
			catch (SQLException e1) {
				throw new SQLException("Cannot close statement");
			}
		}
	}

	public List<ArticleBean> findArticleByUser(String user) throws SQLException {
		if (user == null || user.trim().isEmpty()) {
			return null;
		}
		String query = "SELECT * FROM articolo WHERE id_asta IS NULL AND username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		List<ArticleBean> articleList = new ArrayList<>();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, user);
			result = pstatement.executeQuery();
			while (result.next()) {
				ArticleBean article = new ArticleBean();
				article.setId_articolo(result.getInt("id_articolo"));
				article.setNome_articolo(result.getString("nome"));
				article.setDescrizione_articolo(result.getString("descrizione"));
				article.setUrl_immagine(result.getString("immagine"));
				article.setPrezzo(result.getBigDecimal("prezzo"));
				article.setUser(result.getString("username"));
				article.setId_asta(null);
				articleList.add(article);
			}
		}
		catch (SQLException e){
			throw new SQLException(e);
		}
		finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return articleList;
	}
	
	public List<ArticleBean> findArticleByAuction(int id_auction) throws SQLException {
		if (id_auction < 0) {
			return null;
		}
		List<ArticleBean> articleList = new ArrayList<>();
		String query = "SELECT * FROM articolo WHERE id_asta = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_auction);
			result = pstatement.executeQuery();
			while (result.next()) {
				ArticleBean article = new ArticleBean();
				article.setId_articolo(result.getInt("id_articolo"));
				article.setNome_articolo(result.getString("nome"));
				article.setDescrizione_articolo(result.getString("descrizione"));
				article.setUrl_immagine(result.getString("immagine"));
				article.setPrezzo(result.getBigDecimal("prezzo"));
				article.setUser(result.getString("username"));
				article.setId_asta(null);
				articleList.add(article);
			}
		}
		catch (SQLException e){
			throw new SQLException(e);
		}
		finally {
			try {
				if (result != null) {
					result.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return articleList;
	}
	
 
}
