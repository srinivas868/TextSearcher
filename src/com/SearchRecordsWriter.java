package com;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.csvreader.CsvWriter;


public class SearchRecordsWriter {

	private CsvWriter writer;
	private String csvDir;
	private String csvFileName;
	private String[] headerArray = {"ItemDescriptor","Property", "Using in codebase", "Has data in DB"};
	
	SearchRecordsWriter(String csvDir, String csvFileName){
		this.csvDir = csvDir;
		this.csvFileName = csvFileName;
	}
	
	public void startWriting(List<SearchRecord> recordsList) throws IOException{
		
		String currentItemDescName ="";
		writer = new CsvWriter(new FileWriter(csvDir+"\\"+csvFileName, true), ',');
		writer.writeRecord(headerArray);
		
		for(SearchRecord record : recordsList){
			
			if(!currentItemDescName.equalsIgnoreCase(record.getItemDescriptorName())){
				writer.write(record.getItemDescriptorName());
				currentItemDescName = record.getItemDescriptorName();
			}
			else{
				writer.write("");
			}
			writer.write(record.getPropertyName());
			writer.write("No");
			writer.endRecord();
		}
		writer.close();
	}
}
