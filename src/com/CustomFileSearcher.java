package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lib.XMLProcessor;

public class CustomFileSearcher {

	public static void main(String []args){
		
		String filesDir = "E:\\Plantronics\\ATG\\ATG11.1\\Plantronics\\j2ee-apps\\plt_estore";
		//String definitionFilePath = "C:\\Users\\Nvizion\\Downloads\\data\\customCatalog.xml";
		String definitionFilePath = "E:\\Plantronics\\ATG\\ATG11.1\\Plantronics\\config\\atg\\commerce\\catalog\\custom\\customCatalog.xml";
		String csvDir = "E:\\Nviz\\CSV_files";
		String csvFileName = "Need to be removed.csv";
		String filesTypes = "*.java,*.jsp,*.txt";
		List<SearchRecord> recordsList = new ArrayList<SearchRecord>();
		
		XMLProcessor xmlProcessor = new XMLProcessor(definitionFilePath);
		try {
			recordsList = xmlProcessor.processXML(filesDir, filesTypes);
			System.out.println();
			for(SearchRecord doc : recordsList){
				System.out.println("Document --> "+doc.getPropertyName()+" column name : "+doc.getColumnName()
						+" Hits --> "+doc.getHitsCount());
			}
			SearchRecordsWriter writer = new SearchRecordsWriter(csvDir,csvFileName);
			writer.startWriting(recordsList);
		} catch (IOException e) {
			System.out.println("Unable to Search "+e);
			e.printStackTrace();
		}
		
	}
}
