package com;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.csvreader.CsvWriter;


public class SearchRecordsWriter {

	private CsvWriter writer;
	private String csvDir;
	private String csvFileName;
	private String dbCsvFileName;
	private String[] headerArray = {"ItemDescriptor", "TABLE_NAME", "PROPERTY", "COLUMN_NAME", "USING_IN_CODEBASE", "NOT_NULL_COUNT"};
	private String fileContent = "";
	
	SearchRecordsWriter(String csvDir, String csvFileName, String dbCsvFileName){
		this.csvDir = csvDir;
		this.csvFileName = csvFileName;
		this.dbCsvFileName = dbCsvFileName;
	}
	
	public void startWritingTOCsv(List<SearchRecord> recordsList, String pDerivedProperties) throws IOException{
		
		String currentItemDescName ="";
		writer = new CsvWriter(new FileWriter(csvDir+"\\"+csvFileName, true), ',');
		writer.writeRecord(headerArray);
		
		readCsvFile();
		
		for(SearchRecord record : recordsList){
			
			if(!pDerivedProperties.contains(record.getPropertyName())){
				
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
				writer.write("No");
				String matchedLine = getColumnDataFromCsv(record.getTableName(),record.getColumnName());
				
				if(matchedLine != null){
					int index = matchedLine.lastIndexOf(",");
					String data = matchedLine.substring(index+1);
					writer.write(data);
				}
				else{
					writer.write("");
				}
				writer.endRecord();
			}
		}
		writer.close();
	}
	
	public void readCsvFile(){
		
		FileInputStream inputStream ;
		File csvFile = new File(dbCsvFileName);
		try{
			inputStream = new FileInputStream(csvFile);
			fileContent = IOUtils.toString(inputStream, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("File reading exception "+csvFile.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	public String getColumnDataFromCsv(String tableName, String columnName) {
		
		String matchedLine = null;
		if(tableName == null && columnName == null){
			return matchedLine;
		}
		String regex = constructRegEx(tableName, columnName);
		Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(fileContent);
		
		while(matcher.find()){
			matchedLine = matcher.group();
		}
		return matchedLine;
	}

	private String constructRegEx(String tableName, String columnName) {
		String regex = "(\\b"+tableName+"\\b).*(\\b"+columnName+"\\b).*";
		return regex;
	}

}
