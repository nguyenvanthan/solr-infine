package com.infine.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.junit.Test;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.infine.data.News;

public class RssReader {
	
	private static final String LOCALHOST_SOLR = "http://localhost:8983/solr";
	
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
	
	private String RFC822_FORMAT = "EEE, dd MMM yyyy HH:mm:ss 'GMT'";
	private SimpleDateFormat sdf = new SimpleDateFormat(RFC822_FORMAT,Locale.ENGLISH);

	@Test
	public void writeNews() {
		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			String yahooRssUrl = "http://feeds.finance.yahoo.com/rss/2.0/headline?s=aca.pa&region=FR&lang=fr-FR";
			URL u = new URL(yahooRssUrl); // your feed url

			Document doc = builder.parse(u.openStream());

			NodeList nodes = doc.getElementsByTagName("item");
			
			System.out.println(sdf.format(new Date()));
			
			for (int i = 0; i < nodes.getLength(); i++) {

				Element element = (Element) nodes.item(i);
				System.out.println("Title: "+ getElementValue(element, "title"));
				System.out.println("Link: " + getElementValue(element, "link"));
				System.out.println("Description: " + getElementValue(element, "description"));
				System.out.println("Publish Date: " + getElementValue(element, "pubDate"));
				System.out.println(getDate(getElementValue(element, "pubDate")));
				System.out.println("guid: " + getElementValue(element, "guid"));
				System.out.println();
			}// for
		}// try
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String getCharacterDataFromElement(Element e) {
		try {
			Node child = e.getFirstChild();
			if (child instanceof CharacterData) {
				CharacterData cd = (CharacterData) child;
				return cd.getData();
			}
		} catch (Exception ex) { }
		return "";
	}

	protected float getFloat(String value) {
		if (value != null && !"".equals(value)) {
			return Float.parseFloat(value);
		}
		return 0;
	}

	protected Date getDate(String value) {
		Date retour = null;
		try{
			retour = sdf.parse(value);
		}catch(ParseException e ){
			System.out.println("Bad date : " + value);
		}
		return retour;
	}

	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent.getElementsByTagName(label).item(0));
	}
	
	@Test
	public void feedNewsIndex(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			List<News> listeNews =  generateNewsObjectList();
			int buffer = 1000;
			int nbnews = listeNews.size();
			System.out.println("Nombre d'objets : "+nbnews);
			long start = System.nanoTime();
			List<News> tempList = null;
			while (listeNews != null && listeNews.size() > 0){
				int max = listeNews.size();
				if (buffer < max){
					tempList = listeNews.subList(0, buffer);
				}else {
					tempList = listeNews.subList(0, max);
				}
				solr.addBeans(tempList);
				tempList.clear();
				System.out.print(".");
			}

			// enfin on commit
			solr.commit();
			
			long tempsExecution = System.nanoTime() - start;
			printTime(tempsExecution);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGenerateNewsObjectList(){
		generateNewsObjectList();
	}
	public List<News> generateNewsObjectList(){
		List<News> listeNews = new ArrayList<News>(); 
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("news.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		try {
			String strLine = null;
			String[] temp = null;
			while( (strLine = reader.readLine()) != null){
				temp = strLine.split("\\|");
				if (temp != null & temp.length == 6){
					News n = new News();
					n.setCodeYahoo(temp[0]);
					n.setComposedId(temp[1]);
					n.setDescription(temp[2]);
					n.setTitle(temp[3]);
					n.setLink(temp[4]);
					n.setDatenews(getDate(temp[5]));
					
					listeNews.add( n );
					count ++;
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("liste genere : " + count +" objets");
		return listeNews;
	}
	
	private void printTime(long timeInNanoSec){
		long tempsExecution = TimeUnit.MILLISECONDS.convert(timeInNanoSec, TimeUnit.NANOSECONDS);
		System.out.println("\nTemps d'execution : " + tempsExecution + " ms");
	}
	
	@Test
	public void generateNewsFile(){
		// Fichier de sortie
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("news.txt","utf-8");
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		int count = 0;
		// generation de la liste des codes
		List<String> liste = generateCodesList();
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			for (String codeYahoo : liste) {
				String yahooRssUrl = String.format("http://feeds.finance.yahoo.com/rss/2.0/headline?s=%1$s&region=FR&lang=fr-FR",codeYahoo);
				URL u = new URL(yahooRssUrl);

				Document doc = builder.parse(u.openStream());
				NodeList nodes = doc.getElementsByTagName("item");
				
				for (int i = 0; i < nodes.getLength(); i++) {
					Element element = (Element) nodes.item(i);
					String id = getElementValue(element, "guid");
					if (id != null && !"".equals(id)) {

						writer.append(codeYahoo.toUpperCase());
						writer.append("|").append(id);
						writer.append("|").append(getElementValue(element, "description"));
						writer.append("|").append(getElementValue(element, "title"));
						writer.append("|").append(getElementValue(element, "link"));
						writer.append("|").println(getElementValue(element, "pubDate"));
						
						count++;
						if (count%30 == 0){
							System.out.println(".");
						}else{
							System.out.print(".");
						}
					}
				}
			}
			writer.flush();
			writer.close();
		}catch (Exception ex) {
				ex.printStackTrace();
		}
		System.out.println("\nfichier genere : " +count +" lignes");
	}
	
	public List<String> generateCodesList(){
		List<String> listeRetour = new ArrayList<String>(); 
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("stock.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		try {
			String strLine = null;
			String temp = null;
			while( (strLine = reader.readLine()) != null){
				temp = findCode(strLine);
				if (temp != null && !"".equals(temp.trim())){
					listeRetour.add( temp.toLowerCase() );
					count ++;
					//System.out.println(temp.toLowerCase());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("liste genere : " + count +" objets");
		return listeRetour;
	}
	
	public String findCode(String csvLineData){
		String retour = null;
		if (csvLineData != null && !"".equals(csvLineData.trim())){
			String[] colArray = csvLineData.replaceAll("\"", "").split(";");
			int taille = colArray.length;
			if (taille > 10)
				retour = colArray[10];
		}
		return retour;
	}

}
