package com.infine.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.CredentialsProvider;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.impl.StreamingUpdateSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CoreAdminParams;
import org.apache.solr.util.DateMathParser;
import org.junit.Test;

import com.infine.data.Cours;
import com.infine.data.Portefeuille;
import com.infine.data.Stock;

import static org.junit.Assert.*;

public class SimpleClient {
	
//	private static final String LOCALHOST_SOLR = "http://192.168.56.101:8983/solr";
	private static final String LOCALHOST_SOLR = "http://localhost:8983/solr";
//	private static final String LOCALHOST_SOLR = "http://localhost:8080/solr";

	private static int MAX_ROWS = 1000000;
	
	private static final String[] LIST_USER = {"guest","advanced","basic","admin"};
	private static final int LIST_USER_SIZE = LIST_USER.length;
	
	
	
	@Test
	public void connect(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			SolrQuery solrQuery = new SolrQuery("*:*");
			// nb max de resultat
			solrQuery.setRows(MAX_ROWS);
//			solrQuery.setRows(100);
			// offset
			solrQuery.setStart(0);
			QueryResponse response = solr.query(solrQuery);
			SolrDocumentList liste = response.getResults();
			if (liste != null){
				System.out.println("nombre de resultats : "+liste.size());
				
//				for (SolrDocument doc : liste) {
//					Collection<String> columnNames = doc.getFieldNames();
//					System.out.println( columnNames );
//					for (String cName : columnNames) {
//						System.out.print(doc.getFieldValue(cName)+"|");
//					}
//				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	private void printTime(long timeInNanoSec){
		long tempsExecution = TimeUnit.MILLISECONDS.convert(timeInNanoSec, TimeUnit.NANOSECONDS);
		System.out.println("\nTemps d'execution : " + tempsExecution + " ms");
	}
	
	@Test
	public void findAllCore0(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer("http://localhost:8983/solr/core0");
			SolrQuery solrQuery = new SolrQuery("*:*");
			// nb max de resultat
			solrQuery.setRows(MAX_ROWS);
//			solrQuery.setRows(100);
			// offset
			solrQuery.setStart(0);
			QueryResponse response = solr.query(solrQuery);
			SolrDocumentList liste = response.getResults();
			if (liste != null){
				System.out.println("nombre de resultats : "+liste.size());
				
//				for (SolrDocument doc : liste) {
//					Collection<String> columnNames = doc.getFieldNames();
//					System.out.println( columnNames );
//					for (String cName : columnNames) {
//						System.out.print(doc.getFieldValue(cName)+"|");
//					}
//				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}	
	
	@Test
	public void findAllCore1(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer("http://localhost:8983/solr/core1");
			SolrQuery solrQuery = new SolrQuery("*:*");
			// nb max de resultat
			solrQuery.setRows(MAX_ROWS);
//			solrQuery.setRows(100);
			// offset
			solrQuery.setStart(0);
			QueryResponse response = solr.query(solrQuery);
			SolrDocumentList liste = response.getResults();
			if (liste != null){
				System.out.println("nombre de resultats : "+liste.size());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}	
	
	@Test
	public void findIndx1(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			SolrQuery solrQuery = new SolrQuery("id:BE0003796134-1");
			// nb max de resultat
			solrQuery.setRows(1);
			// offset
			solrQuery.setStart(0);
			QueryResponse response = solr.query(solrQuery);
			SolrDocumentList liste = response.getResults();
			if (liste != null){
				System.out.println("nombre de resultats : "+liste.size());
				
				for (SolrDocument doc : liste) {
					Collection<String> columnNames = doc.getFieldNames();
					System.out.println( columnNames );
					for (String cName : columnNames) {
						System.out.print(doc.getFieldValue(cName)+"|");
					}
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void deleteAll(){
		try {
//			HttpClient client = new HttpClient();
//            AuthScope scope = new AuthScope(AuthScope.ANY_HOST,AuthScope.ANY_PORT,AuthScope.ANY_REALM);
//            client.getState().setCredentials(scope,new UsernamePasswordCredentials("guest", "guest"));
//            client.getParams().setAuthenticationPreemptive(true);
//			List<String> authPrefs = new ArrayList<String>();
//			authPrefs.add(AuthPolicy.BASIC);
//			client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
//			AuthScope scope = new AuthScope("localhost", 8983, AuthScope.ANY_REALM);
//			client.getState().setCredentials(scope, new	UsernamePasswordCredentials("useradmin", "admin"));
//			CommonsHttpSolrServer solr = new CommonsHttpSolrServer("http://localhost:8983/solr",client);
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			String deleteQuery = "*:*";
			UpdateResponse response = null;
			try {
				response = solr.deleteByQuery(deleteQuery);
				solr.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response != null){
				System.out.println("status : "+response.getStatus());
				System.out.println("elapsed time : "+response.getElapsedTime());
				System.out.println("Q time : "+response.getQTime());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void deleteAllPortefeuille(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			String deleteQuery = "listecodeisin:*";
			UpdateResponse response = null;
			try {
				response = solr.deleteByQuery(deleteQuery);
				solr.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response != null){
				System.out.println("status : "+response.getStatus());
				System.out.println("elapsed time : "+response.getElapsedTime());
				System.out.println("Q time : "+response.getQTime());
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Test
	public void feedIndex(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			List<Cours> listeCours = generateCoursList();
			int index = 0;
			int buffer = 10000;
			int nbcours = listeCours.size();
			long start = System.nanoTime();
			List<Cours> tempList = null;
			while (index + buffer < nbcours){
				tempList = listeCours.subList(index, index=index+buffer);
				solr.addBeans(tempList);
				System.out.print(".");
			}
			// ici il doit en rester moins que le buffer
			if (index != nbcours -1){
				tempList = listeCours.subList(index, nbcours-1);
				solr.addBeans(tempList);
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
	public void optimizeIndex(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			long start = System.nanoTime();
			solr.optimize();
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
	public void optimizeIndexAndCommitCore1(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR + "/core1");
			long start = System.nanoTime();
			solr.optimize();
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
	public void feedIndex2(){
		try {
			//Uses an internal MultiThreadedHttpConnectionManager to manage http connections
			StreamingUpdateSolrServer solr = new StreamingUpdateSolrServer(LOCALHOST_SOLR, 1000, 2);
			
			List<Cours> listeCours = generateCoursList();
			int nbcours = listeCours.size();
			System.out.println(nbcours);
			long start = System.nanoTime();

			solr.addBeans(listeCours);
			
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
	public void feedIndex3(){
		try {
			StreamingUpdateSolrServer solr = new StreamingUpdateSolrServer(LOCALHOST_SOLR, 100, 2);
			List<Cours> listeCours = generateCoursList();
			int index = 0;
			int buffer = 1000;
			int nbcours = listeCours.size();
			long start = System.nanoTime();
			List<Cours> tempList = null;
			while (index + buffer < nbcours){
				tempList = listeCours.subList(index, index=index+buffer);
				solr.addBeans(tempList);
			}
			// ici il doit en rester moins que le buffer
			if (index != nbcours -1){
				tempList = listeCours.subList(index, nbcours-1);
				solr.addBeans(tempList);
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
	public void feedIndexCours(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer( LOCALHOST_SOLR);
			List<Cours> listeCours = generateCoursList();
			int index = 0;
			int buffer = 10000;
			int nbcours = listeCours.size();
			long start = System.nanoTime();
			List<Cours> tempList = null;
			while (index + buffer < nbcours){
				tempList = listeCours.subList(index, index=index+buffer);
				solr.addBeans(tempList);
				System.out.print(".");
			}
			// ici il doit en rester moins que le buffer
			if (index != nbcours -1){
				tempList = listeCours.subList(index, nbcours-1);
				solr.addBeans(tempList);
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
	public void feedIndexCoursCore1(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer( LOCALHOST_SOLR + "/core1");
			List<Cours> listeCours = generateCoursList();
			int index = 0;
			int buffer = 10000;
			int nbcours = listeCours.size();
			long start = System.nanoTime();
			List<Cours> tempList = null;
			while (index + buffer < nbcours){
				tempList = listeCours.subList(index, index=index+buffer);
				solr.addBeans(tempList);
				System.out.print(".");
			}
			// ici il doit en rester moins que le buffer
			if (index != nbcours -1){
				tempList = listeCours.subList(index, nbcours-1);
				solr.addBeans(tempList);
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
	public void feedStockIndexCore0(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR +  "/core0");
			List<Stock> listeStocks = generateStockList();
			int buffer = 1000;
			int nbstocks = listeStocks.size();
			System.out.println("Nombre d'objets : "+nbstocks);
			long start = System.nanoTime();
			List<Stock> tempList = null;
			while (listeStocks != null && listeStocks.size() > 0){
				int max = listeStocks.size();
				if (buffer < max){
					tempList = listeStocks.subList(0, buffer);
				}else {
					tempList = listeStocks.subList(0, max);
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
	public void feedStockIndexCore1(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR +  "/core1");
			List<Stock> listeStocks = generateStockList();
			int buffer = 1000;
			int nbstocks = listeStocks.size();
			System.out.println("Nombre d'objets : "+nbstocks);
			long start = System.nanoTime();
			List<Stock> tempList = null;
			while (listeStocks != null && listeStocks.size() > 0){
				int max = listeStocks.size();
				if (buffer < max){
					tempList = listeStocks.subList(0, buffer);
				}else {
					tempList = listeStocks.subList(0, max);
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
	public void feedStockIndex(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			List<Stock> listeStocks = generateStockList();
			int buffer = 1000;
			int nbstocks = listeStocks.size();
			System.out.println("Nombre d'objets : "+nbstocks);
			long start = System.nanoTime();
			List<Stock> tempList = null;
			while (listeStocks != null && listeStocks.size() > 0){
				int max = listeStocks.size();
				if (buffer < max){
					tempList = listeStocks.subList(0, buffer);
				}else {
					tempList = listeStocks.subList(0, max);
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
	public void stressTestFeedStockIndex(){
		int max = 20;
		for (int i = 0; i < max; i++) {
			feedStockIndex();
		}
		System.out.println("FINISH !!!");
	}
	
	@Test
	public void feedPortefeuilleIndex(){
		try {
			CommonsHttpSolrServer solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			List<Portefeuille> listePortefs = generatePortefeuillesList();
			long start = System.nanoTime();
			
			solr.addBeans(listePortefs);
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
	public void swapCore1ToCore0(){
		CommonsHttpSolrServer solr;
		try {
			solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			CoreAdminRequest car = new CoreAdminRequest();
			car.setCoreName("core0");
			car.setOtherCoreName("core1");
			car.setAction(CoreAdminParams.CoreAdminAction.SWAP);
			long start = System.nanoTime();
			CoreAdminResponse carp = car.process(solr);
			long tempsExecution = System.nanoTime() - start;
			printTime(tempsExecution);
			
			System.out.println("CoreAdminResponse : " + carp.getResponse().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Test
	public void mergeIndexes(){
		CommonsHttpSolrServer solr;
		try {
			solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			long start = System.nanoTime();
			CoreAdminResponse carp = CoreAdminRequest.mergeIndexes("solr", new String[] {"G:\\Developpement\\BOULOT\\apache-solr-3.1.0\\example\\multiindexes\\data/index"}, solr);
			long tempsExecution = System.nanoTime() - start;
			printTime(tempsExecution);
			
			System.out.println("CoreAdminResponse : " + carp.getResponse().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public List<Cours> generateCoursList(){
		List<Cours> listeCours = new ArrayList<Cours>(); 
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("test-cours.csv");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try {
			String strLine = null;
			// saute la premiere ligne
			reader.readLine();
			while( (strLine = reader.readLine()) != null){
				listeCours.add( createCoursObject(strLine) );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("liste genere : " + listeCours.size() +" objets");
		return listeCours;
	}
	
	
	public List<Stock> generateStockList(){
		List<Stock> listeStocks = new ArrayList<Stock>(); 
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("stock.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		try {
			String strLine = null;
			Stock temp = null;
			while( (strLine = reader.readLine()) != null){
				temp = createStockObject(strLine);
				if (temp != null){
					listeStocks.add( createStockObject(strLine) );
					count ++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("liste genere : " + count +" objets");
		return listeStocks;
	}
	
	
	public List<Portefeuille> generatePortefeuillesList(){
		
		List<Portefeuille> portefeuillesList = new ArrayList<Portefeuille>(); 
		List<String> listecodes = new ArrayList<String>(); 
		int nbPortefeuilles = 20;

		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("stock.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		// construction de la liste des codes isin
		try {
			String strLine = null;
			Stock temp = null;
			while( (strLine = reader.readLine()) != null){
				String[] colArray = strLine.replaceAll("\"", "").split(";");
				listecodes.add(colArray[9]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// construction des porteuilles 
		Random r = new Random();
		int max = listecodes.size(); // nombre de code isin dans la liste
		int sizeUserList = LIST_USER_SIZE;
		int nbTitresPortefeuille = 0;
		String compteTitre = null;  // chaine contenant la liste des codes isin separe par des espaces
		for (int i = 0; i < nbPortefeuilles; i++) {
			Portefeuille p = new Portefeuille();
			nbTitresPortefeuille = r.nextInt(10) + 1;
			//System.out.println("Nombre de titres dans le portefeuille : " + nbTitresPortefeuille);
			StringBuilder s = new StringBuilder();
			for (int j = 0; j < nbTitresPortefeuille; j++) {
				s.append( listecodes.get(r.nextInt(max)) ).append(" ");
			}
			compteTitre = s.toString().trim();
			//System.out.println("Portefeuille : " + compteTitre);
			p.setListecodeisin(compteTitre);
			p.setCompositeId(compteTitre);
			p.setNameportefeuille(String.format("P-%02d", i));
			// user aleatoire
			p.setUsers(LIST_USER[ r.nextInt(sizeUserList) ] );
			
			// on ajoute le portefeuille a la liste a renvoye
			portefeuillesList.add(p);
		}
		return portefeuillesList;
	}
	
	@Test
	public void testPrint(){
		String temp = String.format("P-%02d", 1);
		System.out.println(temp);
	}

	@Test
	public void generateSynomymesFromStockFile(){
		// Fichier de sortie
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("synonymValeur.txt","utf-8");
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("stock.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		try {
			String strLine = null;
			String temp = null;
			while( (strLine = reader.readLine()) != null){
				temp = constructSynonym(strLine);
				if (temp != null){
					writer.println( temp );
					count++;
				}
			}
			reader.close();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("fichier genere : " +count +" lignes");
	}
	
	@Test
	public void generateSearchTextFile(){
		// Fichier de sortie
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("fields.txt","utf-8");
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("stock.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		try {
			String strLine = null;
			String temp = null;
			while( (strLine = reader.readLine()) != null){
				temp = constructSearchTextFile(strLine);
				if (temp != null){
					writer.print( temp );
					count++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("fichier genere : " +count +" lignes");
	}
	
	public Cours createCoursObject(String csvLineData){
		Cours newInstance = new Cours();
		StringTokenizer tokenizer = new StringTokenizer(csvLineData,",");
		String[] colArray = new String[8];
		int i=0;
		while (tokenizer.hasMoreElements()){
			colArray[i++]=tokenizer.nextToken().replaceAll("\"", "");
		}
		
		newInstance.setCategory(colArray[0]);
		newInstance.setIsin(colArray[1]);
		newInstance.setTime(colArray[2]);
		newInstance.setComposedId(colArray[1]);
		newInstance.setPrice(Float.parseFloat(colArray[4]));
		newInstance.setQuantity(Integer.parseInt(colArray[5]));
		
		return newInstance;
	}

	//"@FF8  ";1646;"FTE FP";"FRANCE TELECOM";"5176177";"France";"EUR";"53";"FTE";"FR0000133308";"FTE.PA";40462;"FTE";"FIXED LINE TELECOMMUNICATIONS";"118101"
	public Stock createStockObject(String csvLineData){
		Stock newInstance = null;
		Random r = new Random();
		int max = LIST_USER_SIZE;
		if (csvLineData != null && !"".equals(csvLineData.trim())){
			String[] colArray = csvLineData.replaceAll("\"", "").split(";");
			int taille = colArray.length;
			if (taille < 10){
				System.out.println("-->  " + csvLineData);
				System.out.println("ligne non prise en compte, il manque trop d'infos");
			}else{
				String codeIsin = colArray[9];
				if (codeIsin != null && codeIsin.length()>0){
					newInstance = new Stock();
					newInstance.setName(colArray[3]);
					newInstance.setPays(colArray[5]);
					newInstance.setDevise(colArray[6]);
					newInstance.setCode(colArray[8]);
					newInstance.setIsin(codeIsin);
					newInstance.setComposedId(codeIsin);
					newInstance.setCodeYahoo(colArray[10]);
					if (taille > 13){
						newInstance.setSecteur(colArray[13]!=null?colArray[13]:"");
					}
					// user aleatoire
					newInstance.setUsers(LIST_USER[ r.nextInt(max) ] );
				}
			}
		}
		
		return newInstance;
	}
	
	/**
	 * Contruit une String correspondant a un synomyme a partir d'une ligne du fichier
	 * @param csvLineData
	 * @return
	 */
	public String constructSynonym(String csvLineData){
		String retour = null;
		String[] colArray = csvLineData.replaceAll("\"", "").split(";");
		StringBuilder sb = new StringBuilder();
		if (colArray[9]!=null && colArray[9].length()>0){
			// code isin
			sb.append(colArray[9].trim()).append("^10.0");
			// code technique 
			if (colArray[8]!=null && colArray[8].length()>0){
				sb.append(",").append(colArray[8].trim()).append("^5.0");
			}
			// nom
			if (colArray[3]!=null && colArray[3].length()>0){
				sb.append(",").append(colArray[3].trim()).append("^8.0");
			}
			retour = sb.toString();
		}
		return retour;
	}
	

	public String constructSearchTextFile(String csvLineData){
		String retour = null;
		String[] colArray = csvLineData.replaceAll("\"", "").split(";");
		StringBuilder sb = new StringBuilder();
		if (colArray[9]!=null && colArray[9].length()>0){
			// code isin
			sb.append(colArray[9].trim()).append("\n");
			// code technique 
			if (colArray[8]!=null && colArray[8].length()>0){
				sb.append(colArray[8].trim()).append("\n");
			}
			// nom
			if (colArray[3]!=null && colArray[3].length()>0){
				sb.append(colArray[3].trim()).append("\n");
			}
			retour = sb.toString();
		}
		return retour;
	}
	
	@Test
	public void testCreateCours(){
		String dataLine = "\"codeisin\",\"NL0000400653\",\"09:44:33\",101,\"19.84\",236,,";
		Cours test = createCoursObject(dataLine);
		System.out.println(test.getCategory());
		System.out.println(test.getIsin());
		System.out.println(test.getTime());
		System.out.println(test.getId());
		System.out.println(test.getPrice());
		System.out.println(test.getQuantity());

	}

	@Test
	public void testAutoReload(){
		long start = System.nanoTime();
		deleteAll();
		feedStockIndex();
		feedPortefeuilleIndex();
		optimizeIndex();
		long tempsExecution = System.nanoTime() - start;
		System.out.println("---------------------------  TOTAL ---------------------------");
		printTime(tempsExecution);
	}
	
	
	@Test
	public void testDate(){
		DateMathParser p = new DateMathParser( TimeZone.getTimeZone("GMT+01:00"), Locale.ENGLISH);
		try {
			/*
			 Map<String, Integer> m = DateMathParser.CALENDAR_UNITS;
			for (Map.Entry<String, Integer> entry : m.entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
			 */
			Date d = p.parseMath("+7DAYS");
			System.out.println(d);
		} catch (ParseException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testPattern(){
		String test = "gap-1321";
		System.out.println(test.matches("\\d.+"));
	}
}
