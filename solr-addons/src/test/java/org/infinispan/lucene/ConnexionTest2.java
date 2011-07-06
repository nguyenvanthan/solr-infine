package org.infinispan.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockFactory;
import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.lucene.locking.TransactionalLockFactory;
import org.infinispan.lucene.readlocks.DistributedSegmentReadLocker;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ConnexionTest2 {

	@Test
	public void putInTransaction() throws InterruptedException, IOException {

		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");

		Cache cache = manager.getCache("solr");
		TransactionManager tm = cache.getAdvancedCache()
				.getTransactionManager();
		try {
			tm.begin();
			String result = (String) cache.put("test", "test");
			System.out.println(result);
			System.out.println("END");
			tm.commit();
			// Thread.sleep(60000L);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void putWithoutTransaction() throws InterruptedException,
			IOException {

		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");

		Cache cache = manager.getCache("solr");
		String result = (String) cache.put("test", "test");
		System.out.println(result);
		System.out.println("END");
		Thread.sleep(60000L);

	}

	@Test
	public void countItemCache() throws InterruptedException,
	IOException {
		
		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");
		Cache cache = manager.getCache("solr");
		System.out.println("Number of items : " + cache.entrySet().size());
		
	}

	@Test
	public void readCache() throws InterruptedException,
	IOException {
		
		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");
		Cache cache = manager.getCache("solr");
		for (Object element : cache.keySet()) {
			Object o = cache.get(element);
			System.out.println(o.getClass().getName());
			System.out.println(o);
			System.out.println("-----------------------------------------------------------------------");
		}
		
	}
	
	@Test
	public void getDirectoryFromCache() throws InterruptedException, IOException {
		
		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");
		Cache cache = manager.getCache("directories");
		for (Object element : cache.keySet()) {
			Object o = cache.get(element);
			System.out.println(o.getClass().getName());
			System.out.println(o);
			System.out.println("-----------------------------------------------------------------------");
		}
		
	}
	
	@Test
	public void resetCache() throws InterruptedException,
	IOException {
		
		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");
		Cache cache = manager.getCache("solr");
		manager.getMembers();
		TransactionManager tm = cache.getAdvancedCache().getTransactionManager();
		try{
		tm.begin();
		cache.stop();
		cache.start();
		tm.commit();
		System.out.println(" ---------------------------------------> RESET !!!");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Test
	public void createInfinispanDirectory() throws InterruptedException,IOException {
		String indexName = "solr";
		EmbeddedCacheManager manager = new DefaultCacheManager("solr-config-file.xml");
		
		Cache cache = manager.getCache(indexName);
		Directory directory = new InfinispanDirectory(
				cache,
				cache,
				indexName, 
				new TransactionalLockFactory(cache, indexName),
				//new BaseLockFactory(cache, INDEX_NAME),
				32 * 1024,
				new DistributedSegmentReadLocker(cache, cache, cache, indexName));
		LockFactory lf = directory.getLockFactory();
		System.out.println(lf.getClass().getName());
		
	}
}
