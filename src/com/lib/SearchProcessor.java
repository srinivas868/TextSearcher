package com.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.SearchRecord;
import com.lib.Searcher;

public class SearchProcessor {

	private Searcher searcher;
	private FileFilter filter = new FileFilter();
	private List<SearchRecord> recordsList = new ArrayList<SearchRecord>();
	private int hitsCount=0;
	private String filesDirectory;
	private String filesTypes;
	private boolean isFileProcessed;
	
	public SearchProcessor(String filesDirectory, String filesTypes) {
		this.filesDirectory = filesDirectory;
		this.filesTypes = filesTypes;
	}
	
	public void processSearch(String keyword, String itemDescName, String columnName, String tableName){
		try {
			startSearch(filesDirectory,filesTypes,keyword);
			if(isFileProcessed() && hitsCount==0){
				SearchRecord doc = new SearchRecord(keyword);
				doc.setHitsCount(hitsCount);
				doc.setColumnName(columnName);
				doc.setItemDescriptorName(itemDescName);
				doc.setTableName(tableName);
				addRecord(doc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startSearch(String dataDirPath, String filesTypes, String keyword)
		      throws IOException{

	  searcher = new Searcher();
      File[] files = new File(dataDirPath).listFiles();

      for (File file : files) {
    	 if(file.isDirectory()){
    		 startSearch(file.getAbsolutePath(),filesTypes,keyword);
    	 }
    	 else{
    		 if(!file.isHidden()
    		            && file.exists()
    		            && file.canRead()
    		            && filter.accept(file,filesTypes)
    		         ){
    		            searcher.search(file,keyword,this); 
    		            setFileProcessed(true);
    		         }
    	 }
      }
   }
	
	public void addRecord(SearchRecord doc){
		recordsList.add(doc);
	}
	
	public void incrementHitsCount(){
		hitsCount++;
	}
	
	public List<SearchRecord> getDocuments(){
		return recordsList;
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
}
