package com.infine.data;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class News extends UserData{
	
	private static final String TECHNICAL_TYPE = "news";
	private static final String PREFIX_ID_VALEUR = TECHNICAL_TYPE + "-";
	
	// RSS yahoo finance
	/*
	 
	 <item>
		<title>Saudi Arabia’s Taqa Appoints Mohammed Rafie as Company Chairman</title>
		<link>http://us.rd.yahoo.com/finance/external/bloomberg/rss/SIG=13hggbb78/*http%3A//www.bloomberg.com/news/2011-05-26/saudi-arabia-s-taqa-appoints-mohammed-rafie-as-company-chairman.html?cmpid=yhoo</link>
		<description></description>
		<guid isPermaLink="false">yahoo_finance/3256031444</guid>
		<pubDate>Thu, 26 May 2011 12:07:16 GMT</pubDate>
	</item>
	 */
	
	@Field
	private String id; 

	@Field
	private String type = TECHNICAL_TYPE; 
	
	@Field
	private String title; 
	
	@Field
	private String codeYahoo; 
	
	@Field
	private String description; 
	
	@Field
	private Date datenews; 

	@Field
	private String link; 
		
	
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


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getCodeYahoo() {
		return codeYahoo;
	}


	public void setCodeYahoo(String codeYahoo) {
		this.codeYahoo = codeYahoo;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Date getDatenews() {
		return datenews;
	}


	public void setDatenews(Date datenews) {
		this.datenews = datenews;
	}


	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}
	
	
}

