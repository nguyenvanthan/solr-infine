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

import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class ConnexionTest2 {
	
	

	
	@Test
	public void discover2() throws InterruptedException, IOException {

		EmbeddedCacheManager manager = new DefaultCacheManager("solr-cours-config-file.xml");
		Cache cache = manager.getCache();
		String result = (String) cache.put("test", "test");
		Thread.sleep(60000L);

	}
}
