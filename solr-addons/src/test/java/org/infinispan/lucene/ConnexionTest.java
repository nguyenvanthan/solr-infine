package org.infinispan.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ConnexionTest {
	
	private static Cache commonCache;
	
	public static void init(){
		/*
		GlobalConfiguration gc = GlobalConfiguration.getClusteredDefault();
//		Properties p = new Properties();
//		p.setProperty("configurationFile", "jgroups-tcp.xml"); // embedded : jgroups-udp.xml(replication), jgroups-tcp.xm(distribution), jgroups-ec2.xml (amazon EC2)
//		gc.setTransportProperties(p);
		Properties transportConfig = gc.getTransportProperties();
		transportConfig.put("clusterName", "SolrCluster");
		transportConfig.put("machineId", "PowerMachine2");
		transportConfig.put("rackId", "Rack01");
		transportConfig.put("siteId", "Elancourt");
		Configuration config = new Configuration();
		config.fluent()
			.clustering()
			.mode(Configuration.CacheMode.REPL_SYNC)
			//.stateRetrieval().fetchInMemoryState(Boolean.TRUE)
		    .invocationBatching();
		
		GlobalConfiguration gc = GlobalConfiguration.getClusteredDefault();
		Configuration config = new Configuration();
		config.setCacheMode(Configuration.CacheMode.REPL_SYNC);
		
		EmbeddedCacheManager manager = new DefaultCacheManager(gc,config);
		commonCache = manager.getCache();
		*/
		try {
			EmbeddedCacheManager manager = new DefaultCacheManager("demo-config-file.xml");
			commonCache = manager.getCache();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void connectToCache(){
		commonCache.put("FR00123", "test value2");
		String test = (String) commonCache.get("FR00123");
		System.out.println("Resultat : " +test);
	}


	@Test
	public void putToCache() throws InterruptedException{
		commonCache.put("FR00456",Double.valueOf("68.59"));
		Thread.sleep(1000);
	}
	
	@Test
	public void getToCache(){
		Double result = (Double) commonCache.get("FR00456");
		System.out.println("result : "+result.doubleValue());
	}
	
	@Test
	public void getToCacheDemo(){
		Object result =  commonCache.get("test");
		System.out.println("result : " + result);
	}
	

	@Test
	public void loadCache() throws IOException{
		EmbeddedCacheManager manager = new DefaultCacheManager("solr-cours-config-file.xml");
		Cache cache = manager.getCache();  
		Set<String> listeStocks = new HashSet<String>(); 
		Random r = new Random();
		// ouverture du fichier
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("stock.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		int count = 0;
		TransactionManager tm = null;
		try {
			String strLine = null;
			String temp = null;
			tm = cache.getAdvancedCache().getTransactionManager();
			tm.begin();
			while( (strLine = reader.readLine()) != null){
				String[] colArray = strLine.replaceAll("\"", "").split(";");
				String codeIsin = colArray[9];
				if (codeIsin != null){
					cache.put(codeIsin, ""+ (r.nextDouble()*100));
					count ++;
				}
			}
			tm.commit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			if (tm!=null){
				try {
					tm.rollback();
				} catch (Exception e1) { }
			}
			e.printStackTrace();
		}
		System.out.println("nombre de cours ajoutes : " + count);
	}
	
	@Test
	public void discover(){
		try {
			EmbeddedCacheManager manager = new DefaultCacheManager("lucene-demo-cache-config.xml");
			Cache cache = manager.getCache();  
			cache.put("test", "test");
			Thread.sleep(10000);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
