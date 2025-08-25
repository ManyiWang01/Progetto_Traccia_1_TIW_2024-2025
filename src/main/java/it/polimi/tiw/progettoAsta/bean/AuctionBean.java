package it.polimi.tiw.progettoAsta.bean;

import java.sql.Timestamp;

public class AuctionBean {
	private int id_asta;
	private float p_iniziale;
	private int min_rialzo;
	private Timestamp data_scadenza;
	private boolean status;
	private String creator;
	private String winner;
	
	public int getId_asta() {
		return id_asta;
	}
	public void setId_asta(int id_asta) {
		this.id_asta = id_asta;
	}
	public float getP_iniziale() {
		return p_iniziale;
	}
	public void setP_iniziale(float p_iniziale) {
		this.p_iniziale = p_iniziale;
	}
	public int getMin_rialzo() {
		return min_rialzo;
	}
	public void setMin_rialzo(int min_rialzo) {
		this.min_rialzo = min_rialzo;
	}
	public Timestamp getData_scadenza() {
		return data_scadenza;
	}
	public void setData_scadenza(Timestamp data_scadenza) {
		this.data_scadenza = data_scadenza;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
}
