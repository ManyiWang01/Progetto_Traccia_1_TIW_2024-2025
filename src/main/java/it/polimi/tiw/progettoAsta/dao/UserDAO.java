package it.polimi.tiw.progettoAsta.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.progettoAsta.bean.UserBean;


public class UserDAO {
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<UserBean> getUserInfo(String username) throws SQLException {
		List<UserBean> userList = new ArrayList<>();
		String query = "SELECT username, nome, cognome, indirizzo FROM user WHERE username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = connection.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				UserBean user = new UserBean();
				user.setUsername(result.getString("username"));
				user.setNome(result.getString("nome"));
				user.setCognome(result.getString("cognome"));
				user.setIndirizzo(result.getString("indirizzo"));
				userList.add(user);
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
		return userList;
	}
	public String getUserPassword(String username) throws SQLException {
		String query = "SELECT password FROM user WHERE username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		String password = null;
		try {
			pstatement = connection.prepareStatement(query);
			pstatement.setString(1, username);
			result = pstatement.executeQuery();
			while(result.next()) {
				password = result.getString("password");
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
		return password;
	}
}
