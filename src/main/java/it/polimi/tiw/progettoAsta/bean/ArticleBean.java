package it.polimi.tiw.progettoAsta.bean;

public class ArticleBean {
	private int id_articolo;
	private String nome_articolo;
	private String descrizione_articolo;
	private String url_immagine;
	private float prezzo;
	private String user;
	private Integer id_asta;
	
	public int getId_articolo() {
		return id_articolo;
	}
	public void setId_articolo(int id_articolo) {
		this.id_articolo = id_articolo;
	}
	public String getNome_articolo() {
		return nome_articolo;
	}
	public void setNome_articolo(String nome_articolo) {
		this.nome_articolo = nome_articolo;
	}
	public String getDescrizione_articolo() {
		return descrizione_articolo;
	}
	public void setDescrizione_articolo(String descrizione_articolo) {
		this.descrizione_articolo = descrizione_articolo;
	}
	public String getUrl_immagine() {
		return url_immagine;
	}
	public void setUrl_immagine(String url_immagine) {
		this.url_immagine = url_immagine;
	}
	public float getPrezzo() {
		return prezzo;
	}
	public void setPrezzo(float prezzo) {
		this.prezzo = prezzo;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Integer getId_asta() {
		return id_asta;
	}
	public void setId_asta(Integer id_asta) {
		this.id_asta = id_asta;
	}

}
