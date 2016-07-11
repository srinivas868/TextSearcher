package com.nviz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lib.SearchRecord;
import com.lib.XMLProcessor;

public class CustomTextSearcher {

	public static void main(String []args){
		
		String moduleDir = "E:\\Plantronics\\git\\plantronics";
		String definitionFilePath = "E:\\Plantronics\\git\\plantronics\\config\\plantronics\\region\\RegionRepository.xml";
		String csvDir = "E:\\Nviz\\CSV_files";
		String csvFileName = "RegionRepository_Need to be removed_2.csv";
		String dbCsvFile = "E:\\Plantronics\\Catalog-cleanup\\DB_Report\\Seabright_CATB.csv";
		String repositoriesDefFilesDir = "E:\\Plantronics\\Catalog-cleanup\\repositories";
		String filesTypes = "*.java,*.jsp,*.jspf";
		//leave it blank if repository is not OOTB
		String ootbCsvFileName = "";
		
		long startTime = (System.currentTimeMillis()/1000);
		
		List<SearchRecord> recordsList = new ArrayList<SearchRecord>();
		
		XMLProcessor xmlProcessor = new XMLProcessor(definitionFilePath, repositoriesDefFilesDir);
		try {
			
			xmlProcessor.processXML(moduleDir, filesTypes);						//reads xml and search
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
