package org.apache.solr.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexReader;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.FastOutputStream;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.StrUtils;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.IndexDeletionPolicyWrapper;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.SolrDeletionPolicy;
import org.apache.solr.core.SolrEventListener;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.BinaryQueryResponseWriter;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.update.DirectUpdateHandler2;
import org.apache.solr.util.RefCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class InfinispanReplicationHandler extends ReplicationHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReplicationHandler.class.getName());
	
	private InfinispanSnapPuller snapPuller;
	
	private ReentrantLock snapPullLock = new ReentrantLock();
	
	private volatile InfinispanSnapPuller tempSnapPuller;
	
	private boolean isMaster = false;

	private boolean isSlave = false;
	
	private boolean replicateOnOptimize = false;

	private boolean replicateOnCommit = false;

	private boolean replicateOnStart = false;

	private NamedList<String> confFileNameAlias = new NamedList<String>();
	
	private String includeConfFiles;
	
	private volatile IndexCommit indexCommitPoint;
	
	private Integer reserveCommitDuration = SnapPuller.readInterval("00:00:10");
	
	@SuppressWarnings("unchecked")
	@Override
	public void inform(SolrCore core) {
	    this.core = core;
	    registerFileStreamResponseWriter();
	    registerCloseHook();
	    NamedList slave = (NamedList) initArgs.get("slave");
	    boolean enableSlave = isEnabled( slave );
	    if (enableSlave) {
	      tempSnapPuller = snapPuller = new InfinispanSnapPuller(slave, this, core);
	      isSlave = true;
	    }
	    NamedList master = (NamedList) initArgs.get("master");
	    boolean enableMaster = isEnabled( master );
	    if (enableMaster) {
	      includeConfFiles = (String) master.get(CONF_FILES);
	      if (includeConfFiles != null && includeConfFiles.trim().length() > 0) {
	        List<String> files = Arrays.asList(includeConfFiles.split(","));
	        for (String file : files) {
	          if (file.trim().length() == 0) continue;
	          String[] strs = file.split(":");
	          // if there is an alias add it or it is null
	          confFileNameAlias.add(strs[0], strs.length > 1 ? strs[1] : null);
	        }
	        LOG.info("Replication enabled for following config files: " + includeConfFiles);
	      }
	      List backup = master.getAll("backupAfter");
	      boolean backupOnCommit = backup.contains("commit");
	      boolean backupOnOptimize = !backupOnCommit && backup.contains("optimize");
	      List replicateAfter = master.getAll(REPLICATE_AFTER);
	      replicateOnCommit = replicateAfter.contains("commit");
	      replicateOnOptimize = !replicateOnCommit && replicateAfter.contains("optimize");

	      // if we only want to replicate on optimize, we need the deletion policy to
	      // save the last optimized commit point.
	      if (replicateOnOptimize) {
	        IndexDeletionPolicyWrapper wrapper = core.getDeletionPolicy();
	        IndexDeletionPolicy policy = wrapper == null ? null : wrapper.getWrappedDeletionPolicy();
	        if (policy instanceof SolrDeletionPolicy) {
	          SolrDeletionPolicy solrPolicy = (SolrDeletionPolicy)policy;
	          if (solrPolicy.getMaxOptimizedCommitsToKeep() < 1) {
	            solrPolicy.setMaxOptimizedCommitsToKeep(1);
	          }
	        } else {
	          LOG.warn("Replication can't call setMaxOptimizedCommitsToKeep on " + policy);
	        }
	      }

	      if (replicateOnOptimize || backupOnOptimize) {
	        core.getUpdateHandler().registerOptimizeCallback(getEventListener(backupOnOptimize, replicateOnOptimize));
	      }
	      if (replicateOnCommit || backupOnCommit) {
	        replicateOnCommit = true;
	        core.getUpdateHandler().registerCommitCallback(getEventListener(backupOnCommit, replicateOnCommit));
	      }
	      if (replicateAfter.contains("startup")) {
	        replicateOnStart = true;
	        // on met a jour les readers d'index
	        updateIndexCommit(core);
	      }
	      String reserve = (String) master.get(RESERVE);
	      if (reserve != null && !reserve.trim().equals("")) {
	        reserveCommitDuration = SnapPuller.readInterval(reserve);
	      }
	      LOG.info("Commits will be reserved for  " + reserveCommitDuration);
	      isMaster = true;
	    }
	  }

	void updateIndexCommit(SolrCore core) {
		RefCounted<SolrIndexSearcher> s = core.getNewestSearcher(false);
		try {
			IndexReader reader = s == null ? null : s.get().getReader();
			if (reader != null && reader.getIndexCommit() != null
					&& reader.getIndexCommit().getGeneration() != 1L) {
				try {
					if (replicateOnOptimize) {
						// on recupere la liste des commits
						Collection<IndexCommit> commits = IndexReader.listCommits(reader.directory());
						for (IndexCommit ic : commits) {
							if (ic.isOptimized()) {
								if (indexCommitPoint == null || indexCommitPoint.getVersion() < ic.getVersion()) {
									indexCommitPoint = ic;
								}
							}
						}
					} else {
						indexCommitPoint = reader.getIndexCommit();
					}
				} finally {
					if (indexCommitPoint != null) {
						core.getDeletionPolicy().saveCommitPoint(indexCommitPoint.getVersion());
					}
				}
			}
			// on met a jour le nouveau writer, normalement on ne devrait pas ecrire directement dans ce core mais bon
			if (core.getUpdateHandler() instanceof DirectUpdateHandler2) {
				((DirectUpdateHandler2) core.getUpdateHandler()).forceOpenWriter();
			} else {
				LOG.warn("The update handler being used is not an instance or sub-class of DirectUpdateHandler2. "
						+ "Replicate on Startup cannot work.");
			}

		} catch (IOException e) {
			LOG.warn("Unable to get IndexCommit on startup", e);
		} finally {
			if (s != null)
				s.decref();
		}
	}
	
	@Override
	void doFetch(SolrParams solrParams) {
	    String masterUrl = solrParams == null ? null : solrParams.get(MASTER_URL);
	    if (!snapPullLock.tryLock())
	      return;
	    try {
	      tempSnapPuller = snapPuller;
	      if (masterUrl != null) {
	        NamedList<Object> nl = solrParams.toNamedList();
	        nl.remove(SnapPuller.POLL_INTERVAL);
	        tempSnapPuller = new InfinispanSnapPuller(nl, this, core);
	      }
	      tempSnapPuller.fetchLatestIndex(core);
	    } catch (Exception e) {
	      LOG.error("SnapPull failed ", e);
	    } finally {
	      tempSnapPuller = snapPuller;
	      snapPullLock.unlock();
	    }
	  }
	
	 // check master or slave is enabled
	  private boolean isEnabled( NamedList params ){
	    if( params == null ) return false;
	    Object enable = params.get( "enable" );
	    if( enable == null ) return true;
	    if( enable instanceof String )
	      return StrUtils.parseBool( (String)enable );
	    return Boolean.TRUE.equals( enable );
	  }
	
	  /**
	   * register a closehook
	   */
	  private void registerCloseHook() {
	    core.addCloseHook(new CloseHook() {
	      public void close(SolrCore core) {
	        if (snapPuller != null) {
	          snapPuller.destroy();
	        }
	      }
	    });
	  }
	  /**
	   * A ResponseWriter is registered automatically for wt=filestream This response writer is used to transfer index files
	   * in a block-by-block manner within the same HTTP response.
	   */
	  private void registerFileStreamResponseWriter() {
	    core.registerResponseWriter(FILE_STREAM, new BinaryQueryResponseWriter() {
	      public void write(OutputStream out, SolrQueryRequest request, SolrQueryResponse resp) throws IOException {
	        FileStream stream = (FileStream) resp.getValues().get(FILE_STREAM);
	        stream.write(out);
	      }

	      public void write(Writer writer, SolrQueryRequest request, SolrQueryResponse response) throws IOException {
	        throw new RuntimeException("This is a binary writer , Cannot write to a characterstream");
	      }

	      public String getContentType(SolrQueryRequest request, SolrQueryResponse response) {
	        return "application/octet-stream";
	      }

	      public void init(NamedList args) { /*no op*/ }
	    });

	  }
	  
	  /**
	   * Register a listener for postcommit/optimize
	   *
	   * @param snapshoot do a snapshoot
	   * @param getCommit get a commitpoint also
	   *
	   * @return an instance of the eventlistener
	   */
	  private SolrEventListener getEventListener(final boolean snapshoot, final boolean getCommit) {
	    return new SolrEventListener() {
	    	  public void init(NamedList args) {/*no op*/ }

		      /**
		       * This refreshes the latest replicateable index commit and optionally can create Snapshots as well
		       */
		      public void postCommit() {
		        IndexCommit currentCommitPoint = core.getDeletionPolicy().getLatestCommit();
	
		        if (getCommit) {
		          // IndexCommit oldCommitPoint = indexCommitPoint;
		          indexCommitPoint = currentCommitPoint;
	
		          // We don't need to save commit points for replication, the SolrDeletionPolicy
		          // always saves the last commit point (and the last optimized commit point, if needed)
		          /***
		          if (indexCommitPoint != null) {
		            core.getDeletionPolicy().saveCommitPoint(indexCommitPoint.getVersion());
		          }
		          if(oldCommitPoint != null){
		            core.getDeletionPolicy().releaseCommitPoint(oldCommitPoint.getVersion());
		          }
		          ***/
		        }
		        if (snapshoot) {
		          try {
		            SnapShooter snapShooter = new SnapShooter(core, null);
		            snapShooter.createSnapAsync(currentCommitPoint, InfinispanReplicationHandler.this);
		          } catch (Exception e) {
		            LOG.error("Exception while snapshooting", e);
		          }
		        }
		      }

		      public void newSearcher(SolrIndexSearcher newSearcher, SolrIndexSearcher currentSearcher) { /*no op*/}
	      };
	    }
	  
	/**
	 * Classe interne FileStream
	 */
	private class FileStream {
		private SolrParams params;

		private FastOutputStream fos;

		private Long indexVersion;
		private IndexDeletionPolicyWrapper delPolicy;

		public FileStream(SolrParams solrParams) {
			params = solrParams;
			delPolicy = core.getDeletionPolicy();
		}

		public void write(OutputStream out) throws IOException {
			String fileName = params.get(FILE);
			String cfileName = params.get(CONF_FILE_SHORT);
			String sOffset = params.get(OFFSET);
			String sLen = params.get(LEN);
			String compress = params.get(COMPRESSION);
			String sChecksum = params.get(CHECKSUM);
			String sindexVersion = params.get(CMD_INDEX_VERSION);
			if (sindexVersion != null)
				indexVersion = Long.parseLong(sindexVersion);
			if (Boolean.parseBoolean(compress)) {
				fos = new FastOutputStream(new DeflaterOutputStream(out));
			} else {
				fos = new FastOutputStream(out);
			}
			FileInputStream inputStream = null;
			int packetsWritten = 0;
			try {
				long offset = -1;
				int len = -1;
				// check if checksum is requested
				boolean useChecksum = Boolean.parseBoolean(sChecksum);
				if (sOffset != null)
					offset = Long.parseLong(sOffset);
				if (sLen != null)
					len = Integer.parseInt(sLen);
				if (fileName == null && cfileName == null) {
					// no filename do nothing
					writeNothing();
				}

				File file = null;
				if (cfileName != null) {
					// if if is a conf file read from config diectory
					file = new File(core.getResourceLoader().getConfigDir(),
							cfileName);
				} else {
					// else read from the indexdirectory
					file = new File(core.getIndexDir(), fileName);
				}
				if (file.exists() && file.canRead()) {
					inputStream = new FileInputStream(file);
					FileChannel channel = inputStream.getChannel();
					// if offset is mentioned move the pointer to that point
					if (offset != -1)
						channel.position(offset);
					byte[] buf = new byte[(len == -1 || len > PACKET_SZ) ? PACKET_SZ
							: len];
					Checksum checksum = null;
					if (useChecksum)
						checksum = new Adler32();
					ByteBuffer bb = ByteBuffer.wrap(buf);

					while (true) {
						bb.clear();
						long bytesRead = channel.read(bb);
						if (bytesRead <= 0) {
							writeNothing();
							fos.close();
							break;
						}
						fos.writeInt((int) bytesRead);
						if (useChecksum) {
							checksum.reset();
							checksum.update(buf, 0, (int) bytesRead);
							fos.writeLong(checksum.getValue());
						}
						fos.write(buf, 0, (int) bytesRead);
						fos.flush();
						if (indexVersion != null && (packetsWritten % 5 == 0)) {
							// after every 5 packets reserve the commitpoint for
							// some time
							delPolicy.setReserveDuration(indexVersion,
									reserveCommitDuration);
						}
						packetsWritten++;
					}
				} else {
					writeNothing();
				}
			} catch (IOException e) {
				LOG.warn("Exception while writing response for params: "
						+ params, e);
			} finally {
				IOUtils.closeQuietly(inputStream);
			}
		}

		/**
		 * Used to write a marker for EOF
		 */
		private void writeNothing() throws IOException {
			fos.writeInt(0);
			fos.flush();
		}
	}


}
