package com.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Document;

import org.apache.commons.io.IOUtils;

import com.SearchRecord;
import com.lib.Searcher;

public class SearchProcessor implements Runnable{

	private int hitsCount;
	private String filesDirectory;
	private String filesTypes;
	private boolean isFileProcessed;
	private String itemDescName;
	private String columnName;
	private String tableName; 
	private String keyword;
	private SearchRecord record;
	private XMLProcessor xmlProcessor;
	
	public SearchProcessor(String filesDirectory, String filesTypes, String keyword, 
								String itemDescName, String columnName, String tableName, XMLProcessor xmlProcessor) {
		this.filesDirectory = filesDirectory;
		this.filesTypes = filesTypes;
		this.itemDescName = itemDescName;
		this.columnName = columnName;
		this.tableName = tableName;
		this.keyword = keyword;
		this.hitsCount = 0;
		this.xmlProcessor = xmlProcessor;
	}
	
	public void processSearch(){
		try {
			startSearch(filesDirectory,filesTypes,keyword);
			if(isFileProcessed() && hitsCount==0){
				this.record = new SearchRecord(keyword);
				record.setHitsCount(hitsCount);
				record.setColumnName(columnName);
				record.setItemDescriptorName(itemDescName);
				record.setTableName(tableName);
				
				xmlProcessor.addRecord(record);
			}
			System.out.println(Thread.currentThread().getName()+" End");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startSearch(String dataDirPath, String filesTypes, String keyword)
		      throws IOException{

      File[] files = new File(dataDirPath).listFiles();

      for (File file : files) {
    	 if(file.isDirectory()){
    		 startSearch(file.getAbsolutePath(),filesTypes,keyword);
    	 }
    	 else{
    		 if(!file.isHidden()
    		            && file.exists()
    		            && file.canRead()
    		            && FileFilter.accept(file,filesTypes)
    		         ){
    		            search(file,keyword,this); 
    		            setFileProcessed(true);
    		         }
    	 }
      }
   }
	
	public void search(File file, String keyword, SearchProcessor searchProcessor){
		
		String regex = Searcher.constructRegEx(keyword);
		Pattern pattern = Pattern.compile(regex);
		FileInputStream inputStream ;
		Pattern commentsPattern;
		Matcher matcher;
		String fileString = "";
		String commentsRegex = Searcher.constructCommentsRegEx(file.getAbsolutePath());
		
		if(commentsRegex != null){
			try {
				inputStream = new FileInputStream(file);
				fileString = IOUtils.toString(inputStream, "UTF-8");
				commentsPattern = Pattern.compile(commentsRegex);
				matcher = commentsPattern.matcher(fileString);
				
				while(matcher.find()){
					fileString = fileString.replace(matcher.group(), "");
				}
				matcher = pattern.matcher(fileString);
				while(matcher.find()){
					searchProcessor.incrementHitsCount();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("File reading exception "+file.getAbsolutePath());
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Did not find regex for matching comments pattern for file "+file.getAbsolutePath());
		}
		
	}
	
	public void incrementHitsCount(){
		hitsCount++;
	}
	
	public void resetFilesData(){
		this.hitsCount=0;
		this.isFileProcessed = false;
	}

	public boolean isFileProcessed() {
		return isFileProcessed;
	}

	public void setFileProcessed(boolean isFileProcessed) {
		this.isFileProcessed = isFileProcessed;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+" Start");
		processSearch();
	}
}
