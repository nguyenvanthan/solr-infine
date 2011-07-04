package com.infine.solr.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.handler.component.QueryComponent;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.handler.component.ShardRequest;

// incubation
public class CoursComponent extends SearchComponent {

	@Override
	public void prepare(ResponseBuilder rb) throws IOException {
		//TODO
		//SolrParams params = rb.req.getParams();
	}

	@Override
	public void process(ResponseBuilder rb) throws IOException {
		// TODO
		//SolrParams params = rb.req.getParams();
		ShardRequest request = new ShardRequest ();
		Map<String, String[]> mapParams = new HashMap<String, String[]>();
		request.params = new ModifiableSolrParams(mapParams);
		rb.addRequest(new QueryComponent(), request);
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
