package com.lib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchRecord implements Serializable{

	private static final long serialVersionUID = 3690604455966562583L;
	
	private List<String> filesList;
	private int lineNumber;
	private String propertyName;
	private int hitsCount;
	private String columnName;
	private String itemDescriptorName;
	private String tableName;
	private boolean isCommonProperty;
	
	public String getItemDescriptorName() {
		return itemDescriptorName;
	}
	public void setItemDescriptorName(String itemDescriptorName) {
		this.itemDescriptorName = itemDescriptorName;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int getHitsCount() {
		return hitsCount;
	}
	public void setHitsCount(int hitsCount) {
		this.hitsCount = hitsCount;
	}
	public List<String> getFilesList() {
		return filesList;
	}
	public void setFilesList(List<String> filesList) {
		this.filesList = filesList;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	public void addFileName(String fileName){
		getFilesList().add(fileName);
	}
	
	public SearchRecord(String fileName, int lineCount, String propertyName) {
		this.filesList = new ArrayList<String>();
		this.filesList.add(fileName);
		this.lineNumber = lineCount;
		this.propertyName = propertyName;
	}
	public SearchRecord(String propertyName) {
		this.propertyName = propertyName;
	}
	public SearchRecord(String itemDescriptorName, String propertyName) {
		this.propertyName = propertyName;
		this.itemDescriptorName = itemDescriptorName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public boolean isCommonProperty() {
		return isCommonProperty;
	}
	public void setCommonProperty(boolean isCommonProperty) {
		this.isCommonProperty = isCommonProperty;
	}
	
}
