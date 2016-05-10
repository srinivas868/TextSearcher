package com.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.SearchRecord;
import com.lib.Searcher;

public class FileSearchProcessor {

	private Searcher searcher;
	private TextFileFilter filter = new TextFileFilter();
	private List<SearchRecord> recordsList = new ArrayList<SearchRecord>();
	private int hitsCount=0;
	private String filesDirectory;
	private String filesTypes;
	
	public FileSearchProcessor(String filesDirectory, String filesTypes) {
		this.filesDirectory = filesDirectory;
		this.filesTypes = filesTypes;
	}
	
	public void processSearch(String keyword, String itemDescName, String columnName){
		try {
			startSearch(filesDirectory,filesTypes,keyword);
			if(hitsCount==0){
				SearchRecord doc = new SearchRecord(keyword);
				doc.setHitsCount(hitsCount);
				doc.setColumnName(columnName);
				doc.setItemDescriptorName(itemDescName);
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
	
	public void resetHitsCount(){
		hitsCount=0;
	}
}
