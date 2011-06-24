package com.infine.solr.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

public class ActionFilterComponent extends SearchComponent {

	
	private static final String FILTER_FIELDVALUE = "valeur";
	private static final String FILTER_FIELDNAME = "type";
	
	
	private static Set<String> filteredTerms = new HashSet<String>();

	static{
		filteredTerms.add("action");
		filteredTerms.add("equity");
	}
	
	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		SolrParams params = rb.req.getParams();
		String query = params.get("q");
		if (query != null){
			System.out.println("-------------------------------------------------------------------");
			System.out.println("Query : " + query);
			System.out.println("-------------------------------------------------------------------");
			String[] terms = query.split(" ");
			if (terms.length > 1){
				boolean replaced = false; // already replaced, to do only once
				for (String t : terms) {
					if (filteredTerms.contains(t)){
						if (!replaced){
							List<Query> filters = rb.getFilters();
							if (filters == null){
								filters = new ArrayList<Query>();
							}
							filters.add(new TermQuery(new Term(FILTER_FIELDNAME, FILTER_FIELDVALUE)));
							rb.setFilters(filters);						
							replaced = true;
							System.out.println("-------------------------------------------------------------------");
							System.out.println("Ajout d'un filtre sur la valeur --> fq=type:valeur");
							System.out.println("-------------------------------------------------------------------");
						}
					}
				}
			}
		}
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		// Nothing
	}

	@Override
	public String getVersion() {
		return "$Revision: Custom de chez In Fine $";
	}

	@Override
	public String getSourceId() {
		return "$Id: ActionFilterComponent.java powered by CNN $";
	}

	@Override
	public String getSource() {
		return "$URL : Under Construction ^_^$";
	}

	@Override
	public String getDescription() {
		return "A Component to filter special term in the query";
	}

}
