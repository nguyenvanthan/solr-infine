package org.apache.solr;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.lucene.index.IndexFileNameFilter;
import org.apache.lucene.store.Directory;
import org.apache.solr.core.StandardDirectoryFactory;
import org.infinispan.Cache;
import org.infinispan.config.Configuration;
import org.infinispan.config.GlobalConfiguration;
import org.infinispan.lucene.InfinispanDirectory;
import org.infinispan.lucene.SolrInfinispanDirectory;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class CopyOfSolrInfinispanDirectoryFactory extends StandardDirectoryFactory {

	private static final String INDEX_NAME = "solr";

//	private static Map<String, SolrInfinispanDirectory> directories = new HashMap<String, SolrInfinispanDirectory>();
	private static Map<String, InfinispanDirectory> directories = new HashMap<String, InfinispanDirectory>();
	
	@SuppressWarnings("unchecked")
	private static Cache commonCache;
	
	static {
		GlobalConfiguration gc = GlobalConfiguration.getClusteredDefault();
		Properties p = new Properties();
		p.setProperty("configurationFile", "jgroups-tcp.xml"); // embedded : jgroups-udp.xml(replication), jgroups-tcp.xm(distribution), jgroups-ec2.xml (amazon EC2)
		gc.setTransportProperties(p);
//		Properties transportConfig = gc.getTransportProperties();
//		transportConfig.put("clusterName", "SolrCluster");
//		transportConfig.put("machineId", "PowerMachine");
//		transportConfig.put("rackId", "Rack01");
//		transportConfig.put("siteId", "Elancourt");
		Configuration config = new Configuration();
		config.fluent()
			.clustering()
			.mode(Configuration.CacheMode.DIST_SYNC)
//			.stateRetrieval().fetchInMemoryState(Boolean.TRUE)
		    .invocationBatching();
		EmbeddedCacheManager manager = new DefaultCacheManager(gc,config);
		commonCache = manager.getCache();
	}

	@Override
	public Directory open(String path) throws IOException {
		synchronized (CopyOfSolrInfinispanDirectoryFactory.class) {
			System.out.println("---------------------------------------------------------------------------------------------");
			System.out.println("PATH : " + path);
			System.out.println("---------------------------------------------------------------------------------------------");
//			SolrInfinispanDirectory directory = directories.get(path);
			InfinispanDirectory directory = directories.get(path);
			if (directory == null) {
				directory = (InfinispanDirectory) openNew(path);
				directories.put(path, directory);
			}
			return directory;
		}
	}

	@Override
	public boolean exists(String path) {
		synchronized (CopyOfSolrInfinispanDirectoryFactory.class) {
//			SolrInfinispanDirectory directory = directories.get(path);
			InfinispanDirectory directory = directories.get(path);
			if (directory == null) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * Non-public for unit-test access only. Do not use directly
	 */
	Directory openNew(String path) throws IOException {
		// Directory directory = new SolrInfinispanDirectory();
		Directory directory = new InfinispanDirectory(commonCache,INDEX_NAME);
		File dirFile = new File(path);
		boolean indexExists = dirFile.canRead();
		if (indexExists) {
			Directory dir = super.open(path);
			IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
			for (String file : dir.listAll()) {
				if (filter.accept(null, file)) {
					dir.copy(directory, file, file);
				}
			}
		}
		return directory;
	}

	

}
