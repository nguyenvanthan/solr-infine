package com.infine.test;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RssReader {
	
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
	
	private String RFC822_FORMAT = "EEE, d MMM yyyy HH:mm:ss Z";

	@Test
	public void writeNews() {
		try {

			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			String yahooRssUrl = "http://feeds.finance.yahoo.com/rss/2.0/headline?s=airp.pa&region=FR&lang=fr-FR";
			URL u = new URL(yahooRssUrl); // your feed url

			Document doc = builder.parse(u.openStream());

			NodeList nodes = doc.getElementsByTagName("item");

			for (int i = 0; i < nodes.getLength(); i++) {

				Element element = (Element) nodes.item(i);
				System.out.println("Title: "+ getElementValue(element, "title"));
				System.out.println("Link: " + getElementValue(element, "link"));
				System.out.println("Description: " + getElementValue(element, "description"));
				System.out.println("Publish Date: " + getElementValue(element, "pubDate"));
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

	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent.getElementsByTagName(label).item(0));
	}

}
