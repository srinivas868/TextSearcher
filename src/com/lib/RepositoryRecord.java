package com.lib;

public class RepositoryRecord {
	
	private String properties;
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProperties() {
		return properties;
	}
	public void setProperties(String properties) {
		this.properties = properties;
	}
	
	public RepositoryRecord(String name){
		this.name = name;
	}
}
