package com.nviz;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class GetReferentialIntegrityDetails {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		File inputFile = null;
		File dataBaseFile = null;
		String csvFileName = null;
		if(args.length < 3){
			usage();
			return;
		} else {
			inputFile = new File(args[0]);
			dataBaseFile = new File(args[1]);
			csvFileName = args[2];
		}
		CSVReader databaseCsvReader = new CSVReader(
				new FileReader(dataBaseFile));
		List<String[]> databaseMetaDataList = databaseCsvReader.readAll();
		
		SAXReader saxReader = new SAXReader();
		List<String[]> headerList = new ArrayList<String[]>();
		// define the header in CSV file
		String[] headerArray = {"TABLE", "TABLE_TYPE",
				"ID-COLUMN-NAME", "PRIMARY_KEY_NAME", "FOREIGN_KEY_NAME",
				"R_TABLE_NAME", "R_CONSTRAINT_NAME","REFERENTIAL_INTEGRITY_MATCHED","OBSERVATIONS" };
		headerList.add(headerArray);
		try {
			// Read the repository definiton file
			Document document = saxReader.read(inputFile);

			// get all the Item-descriptors from the repository definition file
			List<Node> selectNodes = document
					.selectNodes("gsa-template/item-descriptor");
			// Iterate all the Item descriptors
			List<String[]> repostioryDetailsList = new ArrayList<String[]>();
			List<String[]> tempRepostioryDetailsList = new ArrayList<String[]>();
			for (Node node : selectNodes) {
				Element itemDescriptorElement = (Element) node;
				
				// list all the tags inside the item descriptor
				List<Object> elements = itemDescriptorElement.elements();
				
				for (Object object : elements) {
					Element element = (Element) object;
					if (element.getName().equalsIgnoreCase("table")) {
						List<String> detailsList = new ArrayList<String>();
						detailsList.add(element.attributeValue("name"));
						detailsList.add(element.attributeValue("type"));
						detailsList.add(element
								.attributeValue("id-column-name"));
						tempRepostioryDetailsList = addDataBaseMetaData(detailsList,
								databaseMetaDataList,
								element.attributeValue("name"),
								element.attributeValue("id-column-name"),
								element.attributeValue("type"),tempRepostioryDetailsList);
					}
				}
			}
			//System.out.println("hello..");
			tempRepostioryDetailsList = applyValidationRule(tempRepostioryDetailsList,databaseMetaDataList);
			System.out.println("adding data to csv file..");
			CSVWriter writer = new CSVWriter(new FileWriter(csvFileName));
			System.out.println("csv file writting completed..");
			writer.writeAll(headerList, false);

			writer.writeAll(tempRepostioryDetailsList, false);
			writer.close();
			System.out.println("Completed");
		} catch (DocumentException e) {
			System.out.println(e);
		}
	}

	private static List<String[]> applyValidationRule(List<String[]> repostioryDetailsList, List<String[]> databaseMetaDataList) {
		List<String[]> finalList = new ArrayList<String[]>();
		List<String[]> tempList = new ArrayList<String[]>();
		//finalList.addrepostioryDetailsList;
		tempList = repostioryDetailsList;
		
		if (null != repostioryDetailsList && repostioryDetailsList.size() > 0) {
			for(int i = 0; i < repostioryDetailsList.size(); i++){
				//System.out.println("Count is : "+ i + "List size is : "+ repostioryDetailsList.size());
				List<String> tableDetailsList = new ArrayList<String>();
				tableDetailsList.addAll(Arrays.asList(repostioryDetailsList.get(i)));
				List <String>temporaryList=new ArrayList<String>();
				temporaryList.addAll(tableDetailsList);
				System.out.println("Before adding rule : "+ temporaryList);
				if (tableDetailsList.size() > 2) {
					//verifying the table type auxillary or multi
					if (null != tableDetailsList.get(1)	&& (tableDetailsList.get(1).equalsIgnoreCase("auxiliary") || tableDetailsList.get(1).equalsIgnoreCase("multi"))) {
						//Verifying the primary key or foreign key existed or not in the table
						boolean checkPrimaryORForeignKeyExisted = checkPrimaryORForeignKeyExisted(tableDetailsList);
						if(checkPrimaryORForeignKeyExisted) {
							if(null != tableDetailsList.get(3) && !tableDetailsList.get(3).isEmpty()){
								temporaryList.add("PASSED");
								temporaryList.add("");
								finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
							} else if(null != tableDetailsList.get(4) && !tableDetailsList.get(4).isEmpty()){
								boolean matched = isForeignKeyReferenceExisted(tableDetailsList,databaseMetaDataList);
								if(matched){
									temporaryList.add("PASSED");
									temporaryList.add("");
									//temporaryList.toArray(new String[temporaryList.size()]
									finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
								} else {
									temporaryList.add("FAILED");
									temporaryList.add("");
									finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
								}
							}
						} else {
							temporaryList.add("FAILED");
							temporaryList.add("");
							finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
						}
						
						
					} else if (null != tableDetailsList.get(1) && tableDetailsList.get(1).equalsIgnoreCase("primary")) {
						System.out.println("Primary Table name is : "+tableDetailsList.get(0));
						if(null!=tableDetailsList.get(3) &&!(tableDetailsList.get(3)).isEmpty()){
							
							temporaryList.add("PASSED");
							temporaryList.add("");
							//temporaryList.toArray(new String[temporaryList.size()]
							finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
						} else if(null!=tableDetailsList.get(4) && !(tableDetailsList.get(4)).isEmpty()) {
							boolean matched = isForeignKeyReferenceExisted(tableDetailsList,databaseMetaDataList);
							if(matched){
								temporaryList.add("PASSED");
								temporaryList.add("");
								//temporaryList.toArray(new String[temporaryList.size()]
								finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
							} else {
								temporaryList.add("FAILED");
								temporaryList.add("");
								finalList.add(temporaryList.toArray(new String[temporaryList.size()]));
							}
						
						}
					}
				} else {
					finalList.add(tableDetailsList.toArray(new String[tableDetailsList.size()]));
				}
			}
		}
		System.out.println("ApplyValidation rule method completed..");
		finalList.removeAll(Collections.singletonList(null));
		return finalList;
	}

	
	private static boolean isForeignKeyReferenceExisted(List<String> tableDetailsList, List<String[]> tempList){
		boolean matched = false;
		if(null != tableDetailsList && !(tableDetailsList.get(4)).isEmpty()){
			if(null != tempList && tempList.size() > 0){
				for (String[] row : tempList) {
					
					if(null != row[0] && row[0].equalsIgnoreCase(tableDetailsList.get(5))){
						if(!row[2].isEmpty()){
							if(row[2].equalsIgnoreCase(tableDetailsList.get(6))){
								matched = true;
							}
						}
					}
				}
			}
		}
		return matched;
	}
	
	
	private static boolean checkPrimaryORForeignKeyExisted(List<String> tableDetailsList){
		if(!tableDetailsList.get(3).isEmpty() || !tableDetailsList.
				get(4).isEmpty()){
			return true;
		}
		return false;
	}
	
	
	private static List<String[]> addDataBaseMetaData(List<String> detailsList,
			List<String[]> databaseMetaDataList, String tableName,
			String columnName, String tableType, List<String[]> tempRepostioryDetailsList) {
		String primaryKeyName = null;
		String foreignKey = null;
		String rtableName = null;
		String rColumnName = null;
		if (null != databaseMetaDataList && databaseMetaDataList.size() > 0) {
			
			for (String[] rowArray : databaseMetaDataList) {
				
				if (rowArray[0].equalsIgnoreCase(tableName)) {
					if (null != rowArray[3]
							&& rowArray[3].equalsIgnoreCase("Primary_Key")) {
						List<String> tempList = new ArrayList<String>();
						tempList.addAll(detailsList);
						primaryKeyName = rowArray[2];
						tempList.add(primaryKeyName);
						tempList.add("");
						tempList.add("");
						tempList.add("");
						tempRepostioryDetailsList.add(tempList.toArray(new String[tempList.size()]));

					} else if (null != rowArray[3]
							&& rowArray[3].equalsIgnoreCase("Foreign_Key")) {
						List<String> tempList = new ArrayList<String>();
						tempList.addAll(detailsList);
						tempList.add("");
						tempList.add(rowArray[2]);
						tempList.add(rowArray[4]);
						rColumnName = rowArray[5];
						if(null != rColumnName){
							rColumnName = rColumnName.trim();
						}
						tempList.add(rColumnName);
						tempRepostioryDetailsList.add(tempList.toArray(new String[tempList.size()]));
					}
				}
			}
		}
		return tempRepostioryDetailsList;
	}

	
	private static void usage(){
		System.err.println("Usage: GetReferentialIntegrityDetails <repository definition file path> <database csv file name> <output  csv file path>");
		System.err.println("Example: GetReferentialIntegrityDetails D:/workspace/RegionRepository.xml D:/Plantronics/DB.csv D:/Plantronics/Reports/RegionRepositoryReport.csv");
		System.err.println("Do not enclose the file names with in double quotes or single quotes");
		System.err.println("Enter the file names with space separated");
	}
}
