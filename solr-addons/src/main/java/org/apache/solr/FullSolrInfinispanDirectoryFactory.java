package org.apache.solr;

import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.infinispan.Cache;
import org.infinispan.lucene.InfinispanDirectory;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
@SuppressWarnings({"unchecked"})
public class FullSolrInfinispanDirectoryFactory extends SolrInfinispanDirectoryFactory {

	private EmbeddedCacheManager getManagerInstance() {
		if (manager == null){
			System.out.println("---------------------------------------------------------------");
			System.out.println("------------   INITIALIZE OF THE CACHE MANAGER     ------------");
			try {
				manager = new DefaultCacheManager(CONFIG_FILE);
				System.out.println(" --> SUCCESS");
			} catch (IOException e) {
				System.out.println(" --> FAILED");
				e.printStackTrace();
			}
			System.out.println("---------------------------------------------------------------");
		}
		return manager;
	}

	@Override
	public Directory open(String path) throws IOException {
		synchronized (FullSolrInfinispanDirectoryFactory.class) {
			System.out.println("---------------------------------------------------------------------------------------------");
			System.out.println("PATH : " + path);
			System.out.println("---------------------------------------------------------------------------------------------");
			InfinispanDirectory directory = getDirectoryFromCache( path);
			if (directory == null) {
				directory = (InfinispanDirectory) openNew(path);
				//directories.put(path, directory);
				putDirectoryToCache(path, directory);
			}
			return directory;
		}
	}

	@Override
	public boolean exists(String path) {
		synchronized (FullSolrInfinispanDirectoryFactory.class) {
			InfinispanDirectory directory = getDirectoryFromCache( path);
			if (directory == null) {
				return false;
			} else {
				return true;
			}
		}
	}

	private void putDirectoryToCache(String path, InfinispanDirectory directory){
		EmbeddedCacheManager manager = getManagerInstance();
		Cache cache = manager.getCache(DIRECTORIES_CACHE);
		cache.put(path, directory);
	}

	private InfinispanDirectory getDirectoryFromCache(String path){
		EmbeddedCacheManager manager = getManagerInstance();
		Cache cache = manager.getCache(DIRECTORIES_CACHE);
		return (InfinispanDirectory) cache.get(path);
	}
	
	

}
