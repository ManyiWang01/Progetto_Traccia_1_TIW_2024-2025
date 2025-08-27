package it.polimi.tiw.progettoAsta.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OfferBean {
	private int id_asta;
	private String user;
	private BigDecimal p_offerta;
	private Timestamp data_offerta;
	
	public int getId_asta() {
		return id_asta;
	}
	public void setId_asta(int id_asta) {
		this.id_asta = id_asta;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public BigDecimal getP_offerta() {
		return p_offerta;
	}
	public void setP_offerta(BigDecimal p_offerta) {
		this.p_offerta = p_offerta;
	}
	public Timestamp getData_offerta() {
		return data_offerta;
	}
	public void setData_offerta(Timestamp data_offerta) {
		this.data_offerta = data_offerta;
	}
	
}
