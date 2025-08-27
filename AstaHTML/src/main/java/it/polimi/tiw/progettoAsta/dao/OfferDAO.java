package it.polimi.tiw.progettoAsta.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.OfferBean;

public class OfferDAO {
	private Connection connection;
	
	public OfferDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<OfferBean> findOfferByAuction(int id_auction) throws SQLException {
		if (id_auction < 0) {
			return null;
		}
		List<OfferBean> offerList = new ArrayList<>();
		String query = "SELECT username, data_offerta, p_offerta FROM offerta WHERE id_asta = ? ORDER BY data_offerta DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_auction);
			result = pstatement.executeQuery();
			while (result.next()) {
				OfferBean offer = new OfferBean();
				offer.setId_asta(id_auction);
				offer.setUser(result.getString("username"));
				offer.setData_offerta(result.getTimestamp("data_offerta"));
				offer.setP_offerta(result.getBigDecimal("p_offerta"));
				offerList.addLast(offer);
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
		return offerList;
	}
	
	public OfferBean findMaxOffer(int id_auction) throws SQLException {
		if (id_auction < 0) {
			return null;
		}
		OfferBean maxOffer = null;
		String query = "SELECT * FROM offerta WHERE id_asta = ? ORDER BY p_offerta DESC LIMIT 1";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_auction);
			result = pstatement.executeQuery();
			while (result.next()) {
				maxOffer = new OfferBean();
				maxOffer.setId_asta(id_auction);
				maxOffer.setUser(result.getString("username"));
				maxOffer.setData_offerta(result.getTimestamp("data_offerta"));
				maxOffer.setP_offerta(result.getBigDecimal("p_offerta"));
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
		return maxOffer;
	}
	
	public void makeAnOffer(int id_auction, String username, Timestamp data_offerta, BigDecimal p_offerta) throws SQLException {
		if (id_auction < 0 || username == null || username.trim().isEmpty() || data_offerta == null || p_offerta == null || p_offerta.compareTo(new BigDecimal("0")) < 0) {
			return;
		}
		String query = "INSERT INTO asta.offerta (id_asta, username, data_offerta, p_offerta) VALUE(?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setInt(1, id_auction);
			pstatement.setString(2, username);
			pstatement.setTimestamp(3, data_offerta);
			pstatement.setBigDecimal(4, p_offerta);
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
}
