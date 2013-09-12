package it.torvergata.mp;

public class Product {
	private int id;
	private String nome;
	private double prezzo;
	private String scadenza;
	private int disponibilita;
	private String descrizione;
	private String fileImmagine;
	
	
	public Product(int i){
		id=i;
		nome="";
		prezzo=0.0;
		scadenza="";
		disponibilita=0;
		descrizione="";
		fileImmagine="";
	}
	
	public String getFileImmagine() {
		return fileImmagine;
	}

	public void setFileImmagine(String file_immagine) {
		this.fileImmagine = file_immagine;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public double getPrezzo() {
		return prezzo;
	}

	public void setPrezzo(double prezzo) {
		this.prezzo = prezzo;
	}

	public String getScadenza() {
		return scadenza;
	}

	public void setScadenza(String scadenza) {
		this.scadenza = scadenza;
	}

	public int getDisponibilita() {
		return disponibilita;
	}

	public void setDisponibilita(int disponibilita) {
		this.disponibilita = disponibilita;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	

	// Metodi Get
	public int getId() {
	return id;
	}

	
	//Metodi Set
	public void setId(int id) {
		this.id = id;
	}
	

}
