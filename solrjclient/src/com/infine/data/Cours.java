package com.infine.data;

import org.apache.solr.client.solrj.beans.Field;

public class Cours{
	
	
	private static final String TECHNICAL_TYPE = "cours";
	private static final String PREFIX_ID_VALEUR = TECHNICAL_TYPE + "-";
	
	
	//"category","isin","time","id","price","quantity","user","group"

	
	@Field
	private String id;
	
	@Field
	private String type = TECHNICAL_TYPE; // ici donnee custom --> c'est un type technique

	@Field
	private String category;

	@Field
	private String isin;
	
	@Field
	private String time;
	
	@Field
	private float price;
	
	@Field
	private int quantity;
	
	public void setComposedId(String id){
		this.id = PREFIX_ID_VALEUR + id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
