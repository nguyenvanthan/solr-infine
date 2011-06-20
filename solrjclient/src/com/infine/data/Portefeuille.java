package com.infine.data;

import org.apache.solr.client.solrj.beans.Field;

public class Portefeuille extends UserData{
	
	private static final String TECHNICAL_TYPE = "portefeuille";
	private static final String PREFIX_ID_PORTEFEUILLE = TECHNICAL_TYPE + "-";

	@Field
	private String id;
	
	@Field
	private String nameportefeuille;
	
	@Field
	private String listecodeisin;
	
	@Field
	private String type = TECHNICAL_TYPE; // ici donnee custom --> c'est un type technique

	public void setCompositeId(String id) {
		this.id = PREFIX_ID_PORTEFEUILLE + id ;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNameportefeuille() {
		return nameportefeuille;
	}

	public void setNameportefeuille(String nameportefeuille) {
		this.nameportefeuille = nameportefeuille;
	}

	public String getListecodeisin() {
		return listecodeisin;
	}

	public void setListecodeisin(String listecodeisin) {
		this.listecodeisin = listecodeisin;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
		
}