package com.infine.data;

import org.apache.solr.client.solrj.beans.Field;

public class UserData {
	
	@Field
	protected String users;
	
	@Field
	protected String groups;

	public String getUsers() {
		return users;
	}

	public void setUsers(String users) {
		this.users = users;
	}

	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

}
