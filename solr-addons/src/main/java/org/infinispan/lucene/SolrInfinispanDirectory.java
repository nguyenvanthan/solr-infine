package org.infinispan.lucene;

import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.lucene.locking.BaseLockFactory;
import org.infinispan.lucene.readlocks.DistributedSegmentReadLocker;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
//import org.infinispan.util.logging.Log;
//import org.infinispan.util.logging.LogFactory;

public class SolrInfinispanDirectory extends InfinispanDirectory {


	public final static int DEFAULT_BUFFER_SIZE = 16 * 1024;

	//private static final Log log = LogFactory.getLog(SolrInfinispanDirectory.class);

	private static final String indexName = "solr";

	@SuppressWarnings("unchecked")
	private static Cache defaultCache;
	
	protected volatile boolean isOpen = true;

	static {
		EmbeddedCacheManager manager;
			try {
				manager = new DefaultCacheManager("solr-config-file.xml");
				defaultCache = manager.getCache(indexName);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Trying alternative configuration : Default ");
				//GlobalConfiguration config = GlobalConfiguration.getNonClusteredDefault();
				//manager = new DefaultCacheManager( config );
				manager = new DefaultCacheManager();
				defaultCache = manager.getCache();
				
				if (defaultCache == null){
//					log.error("Solr Infinispan Directory creation failed !!", e);
					System.out.println("Solr Infinispan Directory creation failed !!");
				}
			}
	}

	public SolrInfinispanDirectory() {
		super(defaultCache,defaultCache,indexName, new BaseLockFactory(defaultCache, indexName),
				DEFAULT_BUFFER_SIZE, new DistributedSegmentReadLocker(defaultCache, defaultCache, defaultCache, indexName));
	}
	
	public boolean isOpen(){
		return isOpen;
	}
	
	@Override
	public void close() {
		isOpen = false;
	}
	
}
