package org.apache.solr;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexFileNameFilter;
import org.apache.lucene.store.Directory;
import org.apache.solr.core.StandardDirectoryFactory;
import org.infinispan.Cache;
import org.infinispan.lucene.InfinispanDirectory;
import org.infinispan.lucene.locking.TransactionalLockFactory;
import org.infinispan.lucene.readlocks.DistributedSegmentReadLocker;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
@SuppressWarnings({"unchecked","rawtypes"})
public class SolrInfinispanDirectoryFactory extends StandardDirectoryFactory {

	private static final String INDEX_NAME = "solr";

	private static final String DIRECTORIES_CACHE = "directories";

	public static final String CONFIG_FILE = "solr-config-file.xml";
	
	public final static int SEGMENT_SIZE = 32 * 1024;

//	private static Map<String, InfinispanDirectory> directories = new HashMap<String, InfinispanDirectory>();
	
	private EmbeddedCacheManager manager = null;
	
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
		synchronized (SolrInfinispanDirectoryFactory.class) {
			System.out.println("---------------------------------------------------------------------------------------------");
			System.out.println("PATH : " + path);
			System.out.println("---------------------------------------------------------------------------------------------");
//			SolrInfinispanDirectory directory = directories.get(path);
//			InfinispanDirectory directory = directories.get(path);
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
		synchronized (SolrInfinispanDirectoryFactory.class) {
//			SolrInfinispanDirectory directory = directories.get(path);
//			InfinispanDirectory directory = directories.get(path);
			InfinispanDirectory directory = getDirectoryFromCache( path);
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
		EmbeddedCacheManager manager = getManagerInstance();
		Cache cache = manager.getCache(INDEX_NAME);
		Directory directory = new InfinispanDirectory(
				cache,
				cache,
				INDEX_NAME, 
				new TransactionalLockFactory(cache, INDEX_NAME),
				//new BaseLockFactory(cache, INDEX_NAME),
				SEGMENT_SIZE,
				new DistributedSegmentReadLocker(cache, cache, cache, INDEX_NAME));
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
