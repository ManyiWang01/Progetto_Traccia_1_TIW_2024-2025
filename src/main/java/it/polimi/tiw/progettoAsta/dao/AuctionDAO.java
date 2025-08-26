package it.polimi.tiw.progettoAsta.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.AuctionBean;

public class AuctionDAO {
	private Connection connection;
	
	public AuctionDAO(Connection connection) {
		this.connection = connection;
	}
	
	public AuctionBean getAuctionById(int id_asta) throws SQLException {
		if (id_asta < 0) {
			return null;
		}
		String query = "SELECT * FROM asta WHERE id_asta = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		AuctionBean auction = new AuctionBean();
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_asta);
			result = pstatement.executeQuery();
			while (result.next()) {
				auction.setId_asta(result.getInt("id_asta"));
				auction.setP_iniziale(result.getBigDecimal("p_iniziale"));
				auction.setMin_rialzo(result.getInt("min_rialzo"));
				auction.setData_scadenza(result.getTimestamp("data_scadenza"));
				auction.setCreator(result.getString("username"));
				auction.setStatus(result.getBoolean("status"));
				auction.setWinner(result.getString("winner"));
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (result != null) {
					result.close();
				}
			}
			catch (SQLException e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			}
			catch (SQLException e2) {
				throw new SQLException("Cannot close statement");
			}
		}
		return auction;
	}
	
 	public int createAuction(BigDecimal p_iniziale, int min_rialzo, String data_scadenza, String user) throws SQLException {
		int code = 0;
		int last_added_id = -1;
		if (p_iniziale == null || p_iniziale.compareTo(new BigDecimal("0")) < 0 || min_rialzo < 0 ||
				data_scadenza == null || user == null || user.trim().isEmpty()) {
			return last_added_id;
		}
		String add_query = "INSERT INTO asta.asta (p_iniziale, min_rialzo, data_scadenza, username) VALUE (?, ?, ?, ?)";
		String last_added_query = "SELECT id_asta FROM asta ORDER BY id_asta DESC LIMIT 1";
		ResultSet result = null;
		PreparedStatement add_pstatement = null;
		Statement last_added_statement = null;
		try {
			add_pstatement = connection.prepareStatement(add_query);
			add_pstatement.setBigDecimal(1, p_iniziale);
			add_pstatement.setInt(2, min_rialzo);
			add_pstatement.setTimestamp(3, Timestamp.valueOf(data_scadenza));
			add_pstatement.setString(4, user);
			code = add_pstatement.executeUpdate();
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				add_pstatement.close();
			}
			catch (SQLException e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		if (code == 0) {
			// nothing modified in the DB
			return last_added_id;
		}
		try {
			last_added_statement = connection.createStatement();
			result = last_added_statement.executeQuery(last_added_query);
			while (result.next()) {
				last_added_id = result.getInt("id_asta");
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
				if (last_added_statement != null) {
					last_added_statement.close();
				}
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return last_added_id;
	}
	
	public void endAuction(int id_asta, String winner) throws SQLException {
		if (id_asta < 0 || winner.trim().isEmpty()) {
			return;
		}
		String query = "UPDATE asta SET status = 1, winner = ? WHERE id_asta = ?";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, winner);
			pstatement.setInt(2, id_asta);
			pstatement.executeUpdate();
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				pstatement.close();
			}
			catch (SQLException e1) {
				throw new SQLException("Cannot close statement");
			}
		}
	}
	
	public List<AuctionBean> findAuctionByCreator(String username) throws SQLException {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}
		List<AuctionBean> auctionList = new ArrayList<>();
		String query = "SELECT * FROM asta WHERE username = ? ORDER BY data_scadenza ASC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			result = pstatement.executeQuery();
			while (result.next()) {
				AuctionBean auction = new AuctionBean();
				auction.setId_asta(result.getInt("id_asta"));
				auction.setP_iniziale(result.getBigDecimal("p_iniziale"));
				auction.setMin_rialzo(result.getInt("min_rialzo"));
				auction.setData_scadenza(result.getTimestamp("data_scadenza"));
				auction.setCreator(result.getString("username"));
				auction.setStatus(result.getBoolean("status"));
				auction.setWinner(result.getString("winner"));
				auctionList.add(auction);
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (result != null) {
					result.close();
				}
			}
			catch (SQLException e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			}
			catch (SQLException e2) {
				throw new SQLException("Cannot close statement");
			}
		}
		return auctionList;
	}
	
	public List<AuctionBean> findAuctionByWinner(String winner) throws SQLException {
		if (winner == null || winner.trim().isEmpty()) {
			return null;
		}
		List<AuctionBean> auctionList = new ArrayList<>();
		String query = "SELECT * FROM asta WHERE winner = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, winner);
			result = pstatement.executeQuery();
			while (result.next()) {
				AuctionBean auction = new AuctionBean();
				auction.setId_asta(result.getInt("id_asta"));
				auction.setP_iniziale(result.getBigDecimal("p_iniziale"));
				auction.setMin_rialzo(result.getInt("min_rialzo"));
				auction.setData_scadenza(result.getTimestamp("data_scadenza"));
				auction.setCreator(result.getString("username"));
				auction.setStatus(result.getBoolean("status"));
				auction.setWinner(result.getString("winner"));
				auctionList.add(auction);
			}
		}
		catch (SQLException e) {
			throw new SQLException(e);
		}
		finally {
			try {
				if (result != null) {
					result.close();
				}
			}
			catch (SQLException e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				if (pstatement != null) {
					pstatement.close();
				}
			}
			catch (SQLException e2) {
				throw new SQLException("Cannot close statement");
			}
		}
		return auctionList;
	}
	
  	public List<AuctionBean> findAuctionByKey(String key, String username) throws SQLException {
		if (key == null || key.trim().isEmpty() || username == null || username.trim().isEmpty()) {
			return null;
		}
		List<AuctionBean> auctionList = new ArrayList<>();
		Timestamp now = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MINUTES));
		String query = "SELECT * FROM asta WHERE DATE(asta.data_scadenza) > ? AND status = 0 AND username <> ? ORDER BY data_scadenza DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String article_query = "SELECT id_articolo, nome, descrizione FROM articolo WHERE id_asta = ? AND id_articolo IS NOT NULL";
		ResultSet article_result = null;
		PreparedStatement article_pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setTimestamp(1, now);
			pstatement.setString(2, username);
			result = pstatement.executeQuery();
			while (result.next()) {
				try {
					article_pstatement = connection.prepareStatement(article_query);
					article_pstatement.setInt(1, result.getInt("id_asta"));
					article_result = article_pstatement.executeQuery();
					while (article_result.next()) {
						if (article_result.getString("nome").contains(key) || article_result.getString("descizione").contains(key)) {
							AuctionBean auction = new AuctionBean();
							auction.setId_asta(result.getInt("id_asta"));
							auction.setP_iniziale(result.getBigDecimal("p_iniziale"));
							auction.setMin_rialzo(result.getInt("min_rialzo"));
							auction.setData_scadenza(result.getTimestamp("data_scadenza"));
							auction.setCreator(result.getString("username"));
							auction.setStatus(result.getBoolean("status"));
							auction.setWinner(result.getString("winner"));
							auctionList.addLast(auction);
							break;
						}
					}
				}
				catch (SQLException e){
					throw new SQLException(e);
				}
				finally {
					try {
						if (article_result != null) {
							article_result.close();
						}
					} catch (Exception e1) {
						throw new SQLException("Cannot close result");
					}
					try {
						if (article_pstatement != null) {
							article_pstatement.close();
						}
					} catch (Exception e1) {
						throw new SQLException("Cannot close statement");
					}
				}
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
		return auctionList;
	}
}
