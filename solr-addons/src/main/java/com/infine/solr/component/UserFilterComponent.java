package com.infine.solr.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;

public class UserFilterComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
//		SolrParams params = rb.req.getParams();
		HttpServletRequest httprequest = (HttpServletRequest) rb.req.getContext().get("HttpServletRequest");
		if (httprequest != null){
			String groupValue = null;
			httprequest.getUserPrincipal();
			String user = httprequest.getRemoteUser();
			System.out.println("--------------------" + user + "----------------");
			if (httprequest.isUserInRole("basic")){
				//rb.rsp.add(CommonParams.FQ, "group:basic");
				groupValue = "basic";
			}else if (httprequest.isUserInRole("advanced")){
				//rb.rsp.add(CommonParams.FQ, "group:advanced");
				groupValue = "advanced";
			}
			if (groupValue != null){
				List<Query> filters = rb.getFilters();
				if (filters == null){
					filters = new ArrayList<Query>();
				}
				filters.add(new TermQuery(new Term("users", groupValue)));
				rb.setFilters(filters);
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
		return "$Id: UserFilterComponent.java powered by CNN $";
	}

	@Override
	public String getSource() {
		return "$URL : Under Construction ^_^$";
	}

	@Override
	public String getDescription() {
		return "A Component to filter the query by users";
	}

}
