package com;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class GetMeteData {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		
		File inputFile = new File("C:/Nviz/plt/catalogClenup/customCatalog.xml");
		String csvFileName = "C:/Nviz/plt/catalogClenup/customCatalog_metadata_Report.csv";
		SAXReader saxReader = new SAXReader();
		List<String[]> headerList = new ArrayList<String[]>();
		//define the header in CSV file
		String[] headerArray = {"ItemDescriptor","TableName","PropertyName","ColumnName","ItemType","ComponentItemType","DataType(Repository)","Required(Repository)","DataType(DB)","Size in DB","Constraint","FieldLengthAllowedInBCC","FIELD_LENGTH_MATCH","DATA_TYPE_MATCH","REQUIRED_PROPERTY","OBSERVATIONS"};
		headerList.add(headerArray);
		try {
			//Read the repository definiton file
			Document document = saxReader.read(inputFile);
			
			//get all the Item-descriptors from the repository definition file
			List<Node> itemDescriptorNodes = document.selectNodes("gsa-template/item-descriptor");
			
			List<String[]> repostioryDetailsList = new ArrayList<String[]>();
			//Iterate all the Item descriptors
			for (Node node : itemDescriptorNodes) {
				
				List<String> itemDescriptorList = new ArrayList<String>();
				Element itemDescriptorElement = (Element)node;
				itemDescriptorList.add(itemDescriptorElement.attributeValue("name"));
				repostioryDetailsList.add(itemDescriptorList.toArray(new String[itemDescriptorList.size()]));
				// list all the  tags inside the item descriptor
				List<Object> tableElements = itemDescriptorElement.elements();
				for (Object object : tableElements) {
					
					Element element = (Element)object;
						if(element.getName().equalsIgnoreCase("table")){
							
							List<String> detailsList = new ArrayList<String>();
							detailsList.add("");
							detailsList.add(element.attributeValue("name"));
							String currentTableName=element.attributeValue("name");
							repostioryDetailsList.add(detailsList.toArray(new String[detailsList.size()]));
							List<Object> propertiesList = element.elements();
							for (Object propertysObj : propertiesList) {
								
							Element propertyElement=(Element) propertysObj;
								if(propertyElement.getName().equalsIgnoreCase("property")){
									List<String> detailsPropertyList = new ArrayList<String>();
									detailsPropertyList.add("");
									detailsPropertyList.add("");
									detailsPropertyList.add(propertyElement.attributeValue("name"));
									if(null!=propertyElement.attributeValue("column-name")){
									detailsPropertyList.add(propertyElement.attributeValue("column-name"));
									}
									else{
										detailsPropertyList.add(propertyElement.attributeValue("column-names"));
									}
									detailsPropertyList.add(propertyElement.attributeValue("item-type"));
									detailsPropertyList.add(propertyElement.attributeValue("component-item-type"));
									detailsPropertyList.add(propertyElement.attributeValue("data-type"));
									detailsPropertyList.add(propertyElement.attributeValue("required"));
									//calling this method to add DB columns into csv file.
									detailsPropertyList= addDbColumns(detailsPropertyList,currentTableName);
									
									repostioryDetailsList.add(detailsPropertyList.toArray(new String[detailsPropertyList.size()]));
								}	
							}
							
						}
				}
			}
			CSVWriter writer = new CSVWriter(new FileWriter(csvFileName));
			writer.writeAll(headerList,false);
			writer.writeAll(repostioryDetailsList,false);
			writer.close();
			System.out.println("file processing completed..");
		} catch (DocumentException e) {
			System.out.println(e);
		}
	}
 public static List<String> addDbColumns(List<String> detailsPropertyList,String currentTable){

		try {
			int i=0;
			//reading the input csv file.
			@SuppressWarnings("resource")
			CSVReader reader = new CSVReader(new FileReader("C:/Nviz/plt/catalogClenup/Report_1.csv"));
			//reading the complete csv file into list.
			List<String[]> readAll = reader.readAll();
			List<String> numberList=new ArrayList<>();
			List<String> varcharList=new ArrayList<>();
			
			numberList.add("int");
			numberList.add("boolean");
			varcharList.add("string");
			varcharList.add("map");
			varcharList.add("set");
			
			@SuppressWarnings("rawtypes")
			HashMap<String, List> numberMap = new HashMap<String, List>();
			
			@SuppressWarnings("rawtypes")
			HashMap<String, List> varcharMap=new HashMap<String, List>();
			numberMap.put("NUMBER", numberList);
			varcharMap.put("VARCHAR2",varcharList);
			
			for(String[] readRow:readAll){
				
				
				if(currentTable.equalsIgnoreCase(readRow[0]) && detailsPropertyList.get(3).equalsIgnoreCase(readRow[1]) ){
					//adding column datatype in db to csv file.
					detailsPropertyList.add(readRow[3]);
					//adding column size in db to csv file.
					detailsPropertyList.add(readRow[4]);
					//adding constraint of column in db to csv file.
					detailsPropertyList.add(readRow[2]);
					
					//for adding size to specific properties.
					/*Map propertyLenghtFourMap=new HashMap<>();
					Map propertyLenghtOneMap=new HashMap<>();
					Map propertyLenghtTwoMap=new HashMap<>();
					
					propertyLenghtFourMap.put("type", "4000");
					propertyLenghtFourMap.put("id","4000");
					propertyLenghtTwoMap.put("featureNotesDefault", "1024");
					propertyLenghtFourMap.put("defaultMediumDescription","4000");
					propertyLenghtTwoMap.put("commonDescriptionDefault","1000");
					propertyLenghtFourMap.put("defaultMetaKeywords", "4000");
					propertyLenghtFourMap.put("descriptionDefault", "4000");
					propertyLenghtFourMap.put("defaultMetaDescription", "4000");
					propertyLenghtFourMap.put("defaultTagLine", "4000");
					
						if(currentTable.equalsIgnoreCase("dcs_product") || currentTable.equalsIgnoreCase("dcs_SKU")){
							
						if(propertyLenghtFourMap.containsKey(detailsPropertyList.get(2)) && readRow[3].equalsIgnoreCase("VARCHAR2")){
							detailsPropertyList.add("4000");
						}
						else if(propertyLenghtTwoMap.containsKey(detailsPropertyList.get(2)) && readRow[0].equalsIgnoreCase("VARCHAR2")){
							detailsPropertyList.add("1024");
						}
						else if(propertyLenghtTwoMap.containsKey(detailsPropertyList.get(2)) && readRow[0].equalsIgnoreCase("VARCHAR2")){
							detailsPropertyList.add("1000");
						}
						
					}
						else if(readRow[3].equalsIgnoreCase("VARCHAR2")){
							detailsPropertyList.add("255");
						}
						else{
							detailsPropertyList.add("");
						}*/
					
					//Adding Field Length Allowed in BCC .
					if(readRow[3].equalsIgnoreCase("VARCHAR2")){
						detailsPropertyList.add("255");
					}
					else{
						detailsPropertyList.add("");
					}
					
					//checking data size match
					if(detailsPropertyList.get(8).equalsIgnoreCase("NUMBER")|| detailsPropertyList.get(8).equalsIgnoreCase("VARCHAR2") ){
						if(null != detailsPropertyList.get(9) && detailsPropertyList.get(9).toString().equalsIgnoreCase(detailsPropertyList.get(11))){
							detailsPropertyList.add("pass");
						}
						else{
							detailsPropertyList.add("fail");
						}
					}else{
						detailsPropertyList.add("");
					}
					
					
					//checking dataType match by checking DataType (Repository) and DataType(DB)
					if(!(readRow[2].equals(null))){
						
						//numberMap.get("NUMBER").equals(readRow[2])
						if(numberMap.get("NUMBER").contains(detailsPropertyList.get(6)))
						{
							detailsPropertyList.add("pass");
						}
						else if(varcharMap.get("VARCHAR2").contains(detailsPropertyList.get(6))){
							detailsPropertyList.add("pass");
						}
						else if(readRow[2].equalsIgnoreCase("timestamp")){
							detailsPropertyList.add("pass");
						}
						else if(readRow[2].equalsIgnoreCase("date")){
							detailsPropertyList.add("pass");
						}
						else{
							detailsPropertyList.add("fail");
							
						}
						//comparing Required(Repository) and Constraint in DB and setting value to REQUIRED_PROPERTY
							if(null != readRow[2] && null !=detailsPropertyList.get(7) ){
							if(readRow[2].equalsIgnoreCase("NO") && detailsPropertyList.get(7).toString().equalsIgnoreCase("TRUE")){
								detailsPropertyList.add("pass");
							}
							else{
								detailsPropertyList.add("fail");
							}
							}
						else{
							detailsPropertyList.add("");
						}
					}
					
				System.out.println("Table name is"+readRow[0]+"column name is"+readRow[1] + "datatype is"+readRow[3]);
				}
			}
		} catch (Exception e) {
			System.out.println("Exception is " + e);
		}
		return detailsPropertyList;

	
 }
}
