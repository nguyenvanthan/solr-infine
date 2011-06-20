package com.infine.data;

import org.apache.solr.client.solrj.beans.Field;

public class Cours {
	
	//"category","isin","time","id","price","quantity","user","group"

	
	@Field
	String id;

	@Field
	String category;

	@Field
	String isin;
	
	@Field
	String time;
	
	@Field
	float price;
	
	@Field
	int quantity;
	
	@Field
	String user;
	
	@Field
	String group;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}


}
