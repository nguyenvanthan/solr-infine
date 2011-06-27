package org.infinispan.lucene;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class SolrInfinispanDirectoryTest {
	
	
	
	public static void main(String[] args) {
//		EmbeddedCacheManager manager = new DefaultCacheManager(	GlobalConfiguration.getClusteredDefault() );
//		Cache c = manager.getCache();
//		c.put("123", "123");
//		Object o = c.get("123");
//		System.out.println("Result:" + o);
		
		SolrInfinispanDirectory d = new SolrInfinispanDirectory();
		System.out.println(d.getIndexName());
//		EmbeddedCacheManager manager;
//		try {
//			manager = new DefaultCacheManager("solr-config-file.xml");
//			Cache defaultCache = manager.getCache("solr");
//			System.out.println( defaultCache.getName());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
	

}
