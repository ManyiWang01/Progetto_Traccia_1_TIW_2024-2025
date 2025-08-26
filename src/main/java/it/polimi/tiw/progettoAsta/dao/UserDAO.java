package it.polimi.tiw.progettoAsta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.progettoAsta.bean.UserBean;


public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public UserBean getUserInfo(String username) throws SQLException {
		if (username == null || username.trim().isEmpty()) {
			return null;
		}
		UserBean user = new UserBean();
		String query = "SELECT username, nome, cognome, indirizzo FROM user WHERE username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				user.setUsername(result.getString("username"));
				user.setNome(result.getString("nome"));
				user.setCognome(result.getString("cognome"));
				user.setIndirizzo(result.getString("indirizzo"));
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
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
		return user;
	}
	
	public boolean checkUserPassword(String username, String password) throws SQLException {
		boolean checked = false;
		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			return checked;
		}
		String query = "SELECT password FROM user WHERE username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			result = pstatement.executeQuery();
			while(result.next()) {
				if (password.equals(result.getString("password"))) {
					checked = true;
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
		return checked;
	}
}
