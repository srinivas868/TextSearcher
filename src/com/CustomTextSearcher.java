package com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lib.XMLProcessor;

public class CustomTextSearcher {

	public static void main(String []args){
		
		String filesDir = "E:\\Plantronics\\ATG\\ATG11.1\\Plantronics";
		String definitionFilePath = "E:\\Plantronics\\ATG\\ATG11.1\\Plantronics\\config\\atg\\commerce\\catalog\\custom\\customCatalog.xml";
		//String definitionFilePath = "C:\\Users\\Nvizion\\Downloads\\data\\customCatalog.xml";
		String csvDir = "E:\\Nviz\\CSV_files";
		String csvFileName = "Need to be removed_13May_v2.csv";
		String dbCsvFile = "E:\\Plantronics\\Catalog cleanup\\Report_1.csv";
		String filesTypes = "*.java,*.jsp,*.jspf,*.txt";
		String ootbCsvFileName = "E:\\Nviz\\CSV_files\\Catalog_OOTB_Properties.csv";
		
		long startTime = (System.currentTimeMillis()/1000);
		
		List<SearchRecord> recordsList = new ArrayList<SearchRecord>();
		
		XMLProcessor xmlProcessor = new XMLProcessor(definitionFilePath);
		try {
			
			xmlProcessor.processXML(filesDir, filesTypes);						//reads xml and search
			recordsList = xmlProcessor.getDocuments();
			System.out.println("Done processing XML");
			for(SearchRecord doc : recordsList){
				System.out.println("Document --> "+doc.getPropertyName()+" column name : "+doc.getColumnName());
			}
			System.out.println("Derived properties "+xmlProcessor.getDerivedProperties());
			System.out.println("Started writing to CSV ");
			SearchRecordsWriter writer = new SearchRecordsWriter(csvDir, csvFileName, dbCsvFile, ootbCsvFileName);
			writer.startWritingTOCsv(recordsList,xmlProcessor.getDerivedProperties());
			
			System.out.println("Time taken -- >> "+((System.currentTimeMillis()/1000) - startTime));
		} catch (IOException e) {
			System.out.println("Unable to Search "+e);
			e.printStackTrace();
		}
		
	}
}
