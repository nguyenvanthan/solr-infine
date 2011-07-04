package org.apache.lucene.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.lucene.store.Directory;
import org.apache.solr.core.IndexReaderFactory;
import org.infinispan.lucene.InfinispanDirectory;

public class InfinispanIndexReaderFactory extends IndexReaderFactory {
	
	private static Map<String, ReadDirectoryReader> readDirectoryReaders = new ConcurrentHashMap<String, ReadDirectory>();

	private class ReadDirectoryReader {
		Directory directory;
		SegmentInfos infos;
		IndexDeletionPolicy deletionPolicy;
		int termInfosIndexDivisor;
		public ReadDirectoryReader(Directory directory, SegmentInfos infos,
				IndexDeletionPolicy deletionPolicy, int termInfosIndexDivisor) {
			super();
			this.directory = directory;
			this.infos = infos;
			this.deletionPolicy = deletionPolicy;
			this.termInfosIndexDivisor = termInfosIndexDivisor;
		}
	}
	
	@Override
	public IndexReader newReader(Directory indexDir, final boolean readOnly)
			throws IOException {
		return open(indexDir, null, null, readOnly, termInfosIndexDivisor);
	}
	
	private IndexReader open(final Directory directory,
			final IndexDeletionPolicy deletionPolicy, final IndexCommit commit,
			final boolean readOnly, final int termInfosIndexDivisor)
			throws CorruptIndexException, IOException {
		return (IndexReader) new SegmentInfos.FindSegmentsFile(directory) {
			@Override
			protected Object doBody(String segmentFileName)
					throws CorruptIndexException, IOException {
				SegmentInfos infos = new SegmentInfos();
				infos.read(directory, segmentFileName);
				if (readOnly)
					return createReadDirectoryReader(directory, infos, deletionPolicy, termInfosIndexDivisor,segmentFileName);
				else
					return new DirectoryReader(directory, infos,
							deletionPolicy, false, termInfosIndexDivisor, null);
			}
		}.run(commit);
	}
	
	private synchronized ReadOnlyDirectoryReader createReadDirectoryReader(
			final Directory directory,
			final SegmentInfos infos, 
			final IndexDeletionPolicy deletionPolicy,
			final int termInfosIndexDivisor, 
			String segmentFileName) throws CorruptIndexException, IOException {
		// on garde le reader en memoire
		ReadOnlyDirectoryReader reader = new ReadOnlyDirectoryReader(
				directory, infos, deletionPolicy, termInfosIndexDivisor, null);
		
		// Faire quelque chose...
		// Raaah c'est les SolrIndexSearchers qu'ils faut mettre a jour !! :'(
		
		// creation d'un objet de sauvegarde
		ReadDirectoryReader r = new ReadDirectoryReader(directory, infos, deletionPolicy, termInfosIndexDivisor);
		// Mise a jour des anciens readers
		if (!readDirectoryReaders.isEmpty()) {
			updateReadOnlyReaders(reader);
		}
		readDirectoryReaders.put(segmentFileName,r);
		
		return reader;
	}
	
	private void updateReadOnlyReaders(ReadOnlyDirectoryReader reader) {
		for(String key : readDirectoryReaders.keySet()){
			readDirectoryReaders.get(key);
			// Salete de Segment de DirectoryReader pourquoi t'es en final !!!
			
		}
		
	}

	/* (non-Javadoc)
	   * @see org.apache.solr.core.IndexReaderFactory#newReader(org.apache.lucene.store.Directory, boolean)
	   */
//	  @Override
	  public IndexReader newReaderOld(Directory indexDir, boolean readOnly)
	      throws IOException {
	    return IndexReader.open(indexDir, null, readOnly, termInfosIndexDivisor);
	  }

}
