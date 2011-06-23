package com.infine.solr.android;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TestSolrJAndroidActivity extends Activity {
	
	private static final String LOCALHOST_SOLR = "http://192.168.1.107:8983/solr";

	private static int MAX_ROWS = 50;
	
	private static final String[] LIST_USER = {"guest","advanced","basic","admin"};
	private static final int LIST_USER_SIZE = LIST_USER.length;
	
	private static ListView listView;
	private static ArrayList<String> list;
	private ListAdapter listAdapter;
	
	private CommonsHttpSolrServer solr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        Intent intent = getIntent();
        String query = null;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            query = intent.getStringExtra(SearchManager.QUERY);
            //showResults(query);
        }
        
        list = new ArrayList<String>();
        try {
			solr = new CommonsHttpSolrServer(LOCALHOST_SOLR);
			SolrQuery solrQuery = null;
			if (query != null) {
				solrQuery = new SolrQuery(query);
			} else {
				solrQuery = new SolrQuery("*:*");
			}
			
			solrQuery.setRows(MAX_ROWS);
			solrQuery.setStart(0);
			QueryResponse response = solr.query(solrQuery);
			SolrDocumentList liste = response.getResults();
			for (SolrDocument solrDocument : liste) {
				list.add(solrDocument.getFieldValue("name").toString());
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		
		listView = (ListView) this.findViewById(R.id.list2);
		listAdapter = new ListAdapter();
		listView.setAdapter(listAdapter);
    }
    
    @Override
    public boolean onSearchRequested() {

        return super.onSearchRequested();
    }
    
    
    private class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
                if (list == null)
                        return 0;
                return list.size();
        }

        @Override
        public Object getItem(int position) {
                return list.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

		@Override
		public View getView(int position, View arg1, ViewGroup parent) {
			 LayoutInflater inflater=getLayoutInflater(); 
			 View row=inflater.inflate(R.layout.list_item_results, parent, false); 
			    
			 TextView tv = (TextView)row.findViewById(R.id.item); 
			 tv.setText(list.get(position));
			 return(row);
		}
		        
    }
}