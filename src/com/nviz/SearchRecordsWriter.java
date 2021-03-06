package com.nviz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.lib.SearchRecord;
import com.csvreader.CsvWriter;


public class SearchRecordsWriter {

	private CsvWriter writer;
	private String csvDir;
	private String csvFileName;
	private String dbCsvFileName;
	private String[] headerArray = {"ItemDescriptor", "TABLE_NAME", "PROPERTY", "COLUMN_NAME", 
									"USING_IN_CODEBASE", "NOT_NULL_COUNT", "COMMON_PROPERTY"};
	private String dbFileContent;
	private String ootbFileContent;
	private String ootbCsvFileName;
	
	SearchRecordsWriter(String csvDir, String csvFileName, String dbCsvFileName, String ootbCsvFileName){
		this.csvDir = csvDir;
		this.csvFileName = csvFileName;
		this.dbCsvFileName = dbCsvFileName;
		this.ootbCsvFileName = ootbCsvFileName;
	}
	
	public void startWritingTOCsv(List<SearchRecord> recordsList, String pDerivedProperties) throws IOException{
		
		String currentItemDescName ="";
		writer = new CsvWriter(new FileWriter(csvDir+"\\"+csvFileName, true), ',');
		writer.writeRecord(headerArray);
		
		this.dbFileContent = readCsvFile(dbCsvFileName);							//read db data csv
		if(ootbCsvFileName != null && !ootbCsvFileName.equalsIgnoreCase("")){
			this.ootbFileContent = readCsvFile(ootbCsvFileName);				//read ootb properties info csv
		}
		
		Collections.sort(recordsList, new ItemDescNameComparator());
		for(SearchRecord record : recordsList){
			
			boolean commonProperty = record.isCommonProperty();
			if(!pDerivedProperties.contains(record.getPropertyName()) && 
					!isOOTBProperty(record.getItemDescriptorName(), record.getPropertyName())){
				
				if(!currentItemDescName.equalsIgnoreCase(record.getItemDescriptorName())){
					writer.write(record.getItemDescriptorName());
					currentItemDescName = record.getItemDescriptorName();
				}
				else{
					writer.write("");
				}
				writer.write(record.getTableName());
				writer.write(record.getPropertyName());
				writer.write(record.getColumnName());
				if(commonProperty){
					writer.write("Yes");
				}
				else{
					writer.write("No");
				}

				String matchedLine = null;
				String columnName = record.getColumnName();
				if(columnName == null || columnName.equalsIgnoreCase("")){
					matchedLine = getColumnDataFromCsv(record.getTableName(),record.getPropertyName());
				}
				else{
					matchedLine = getColumnDataFromCsv(record.getTableName(),columnName);
				}
				
				if(matchedLine != null){
					int index = matchedLine.lastIndexOf(",");
					String data = matchedLine.substring(index+1);
					writer.write(data);
				}
				else{
					writer.write("");
				}
				if(commonProperty){
					writer.write("Yes");
				}
				else{
					writer.write("No");
				}
				writer.endRecord();
			}
		}
		writer.close();
		System.out.println("Completed writing contents to CSV ---->> "+csvDir+"\\"+csvFileName);
	}
	
	public String getColumnDataFromCsv(String tableName, String columnName) {
		
		String matchedLine = null;
		if(tableName == null && columnName == null){
			return matchedLine;
		}
		String regex = constructRegEx(tableName, columnName);
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(dbFileContent);
		
		while(matcher.find()){
			matchedLine = matcher.group();
		}
		return matchedLine;
	}

	public boolean isOOTBProperty(String itemName, String propertyName) {
		
		if(ootbFileContent == null){
			return false;
		}
		String regex = constructRegEx(itemName, propertyName);
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(ootbFileContent);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	public String constructRegEx(String arg1, String arg2) {
		return "(\\b"+arg1+"\\b).*(\\b"+arg2+"\\b).*";
	}
	
	public String readCsvFile(String csvFileName){
		
		if(csvFileName == null || csvFileName.equalsIgnoreCase("")){
			return null;
		}
		FileInputStream inputStream ;
		File csvFile = new File(csvFileName);
		try{
			inputStream = new FileInputStream(csvFile);
			return IOUtils.toString(inputStream, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File reading exception "+csvFile.getAbsolutePath());
			e.printStackTrace();
		}
		return null;
	}
}

class ItemDescNameComparator implements Comparator<SearchRecord>{

	@Override
	public int compare(SearchRecord o1, SearchRecord o2) {
		return o1.getItemDescriptorName()
				.compareToIgnoreCase(o2.getItemDescriptorName());
	}
	
}