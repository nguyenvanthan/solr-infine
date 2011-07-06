package org.apache.solr.handler;

import static org.apache.solr.handler.ReplicationHandler.CMD_INDEX_VERSION;
import static org.apache.solr.handler.ReplicationHandler.GENERATION;
import static org.apache.solr.handler.ReplicationHandler.MASTER_URL;

import java.io.IOException;

import org.apache.lucene.index.IndexCommit;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.search.SolrIndexReader;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.util.RefCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class InfinispanSnapPuller extends SnapPuller{
	
	private static final Logger LOG = LoggerFactory.getLogger(SnapPuller.class.getName());
	
	private final SolrCore solrCore;	
	
	private final String masterUrl;
	
	private final InfinispanReplicationHandler replicationhandler;

	public InfinispanSnapPuller(NamedList initArgs, ReplicationHandler handler,
			SolrCore core) {
		this(initArgs,(InfinispanReplicationHandler) handler, core);
	}
	
	public InfinispanSnapPuller(NamedList initArgs, InfinispanReplicationHandler handler, SolrCore core) {
		super(initArgs, handler, core);
		solrCore = core;
		replicationhandler = handler;
		masterUrl = (String) initArgs.get(MASTER_URL);
	    if (masterUrl == null)
	      throw new SolrException(SolrException.ErrorCode.SERVER_ERROR,
	              "'masterUrl' is required for a slave");
	}

	@Override
	public boolean fetchLatestIndex(SolrCore core) throws IOException {
		long replicationStartTime = System.currentTimeMillis();
		try {
			// get the current 'replicateable' index version in the master
			NamedList response = null;
			try {
				// recupere un long correspond a la derniere version
				response = getLatestVersion();
			} catch (Exception e) {
				LOG.error("Master at: " + masterUrl
						+ " is not available. Index fetch failed. Exception: "
						+ e.getMessage());
				return false;
			}
			long latestVersion = (Long) response.get(CMD_INDEX_VERSION);
			long latestGeneration = (Long) response.get(GENERATION);
			if (latestVersion == 0L) {
				// there is nothing to be replicated
				return false;
			}
			IndexCommit commit;
			RefCounted<SolrIndexSearcher> searcherRefCounted = null;
			SolrIndexReader reader = null;
			try {
				searcherRefCounted = core.getNewestSearcher(false);
				reader = searcherRefCounted.get().getReader();
				commit = reader.getIndexCommit();
			} finally {
				if (searcherRefCounted != null)
					searcherRefCounted.decref();
			}
			if (commit.getVersion() == latestVersion && commit.getGeneration() == latestGeneration) {
				// master and slave are already in sync just return
				LOG.info("Slave in sync with master.");
				return false;
			}else{
				// mise a jour du searcher
				reader.reopen();
				core.closeSearcher();
				
			}
			LOG.info("Master's version: " + latestVersion + ", generation: "
					+ latestGeneration);
			LOG.info("Slave's version: " + commit.getVersion()
					+ ", generation: " + commit.getGeneration());
		} catch (Exception e) {
			return false;
		}
		long replicationTotalTime = System.currentTimeMillis() - replicationStartTime;
		LOG.info("Fetch Time : " + replicationTotalTime + " ms");
		return true;
	}

}
