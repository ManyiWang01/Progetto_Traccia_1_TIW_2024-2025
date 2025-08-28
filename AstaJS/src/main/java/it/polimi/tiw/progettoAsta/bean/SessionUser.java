package it.polimi.tiw.progettoAsta.bean;

import java.sql.Timestamp;

public class SessionUser {
	private String username;
	private Timestamp loginTime;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Timestamp getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Timestamp loginTime) {
		this.loginTime = loginTime;
	}
	
}
