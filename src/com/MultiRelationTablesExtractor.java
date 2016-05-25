package com;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import constants.SearchConstants;

public class MultiRelationTablesExtractor {
	
	private static final String SHARED_TABLE_SEQUENCE = "shared-table-sequence";
	private static final String MULTI = "multi";
	private static final String TYPE = "type";
	private Map<String,Integer> sharedTablesMap;
	private String sharedTables;
	
	public MultiRelationTablesExtractor(){
		this.sharedTablesMap = new HashMap<String,Integer>();
		this.sharedTables = "";
	}

	public static void main(String[] args) {
		
		String xmlFilePath = "E:\\Nviz\\CSV_files\\customCatalog_plt.xml";
		MultiRelationTablesExtractor extractor = new MultiRelationTablesExtractor();
		try {
			System.out.println("Reading XML file -----------> "+xmlFilePath);
			extractor.processXML(xmlFilePath);
			System.out.println("Completed Reading ---------------> "+xmlFilePath);
			System.out.println("Shared tables -- > "+extractor.sharedTables);
		} catch (IOException e) {
			System.out.println("XML Process exception"+e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void processXML(String xmlFilePath) throws IOException {
		
		File inputFile = new File(xmlFilePath);
		SAXReader saxReader = new SAXReader();
		
		try {
			Document document = saxReader.read(inputFile);
			
			List<Node> selectNodes = document.selectNodes(SearchConstants.ROOT_NODE);
			for (Node node : selectNodes) {
				Element itemDescriptorElement = (Element)node;
				String itemName = itemDescriptorElement.attributeValue(SearchConstants.NAME);
				System.out.println("Item descriptor ==========================="+itemName);
				List<Object> elements = itemDescriptorElement.elements();
				Element tableElement = null;
				for (Object object : elements) {
					Element element = (Element)object;
					int prev_shared_table_cnt = 0;
						if(element.getName().equalsIgnoreCase(SearchConstants.TABLE))
						{
							tableElement = (Element)element;
							String tableName = tableElement.attributeValue(SearchConstants.NAME);
							
							if(checkForMultiConstraint(tableElement)){
								int shared_table_count = Integer.valueOf(tableElement.attributeValue(SHARED_TABLE_SEQUENCE));
								if(sharedTablesMap.get(tableName) != null){
									prev_shared_table_cnt = sharedTablesMap.get(tableName);
								}
								
								if(prev_shared_table_cnt == 0){
									sharedTablesMap.put(tableName, shared_table_count);
								}
								else{
									if(prev_shared_table_cnt ==1 && shared_table_count ==2){
										addRecord(tableName);
									}
									else if(prev_shared_table_cnt ==2 && shared_table_count ==1){
										addRecord(tableName);
									}
								}
							}
						}
				}
			}
			
			System.out.println(SearchConstants.COMPLETED);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	private boolean checkForMultiConstraint(Element tableElement) {
		
		if(tableElement.attributeValue(TYPE) != null 
				&& tableElement.attributeValue(TYPE).equalsIgnoreCase(MULTI)
				&& tableElement.attributeValue(SHARED_TABLE_SEQUENCE) != null){
			return true;
		}
		return false;
		
	}
	
	public void addRecord(String tableName){
		
		if(sharedTables.equalsIgnoreCase("")){
			sharedTables= tableName;
		}
		else{
			sharedTables+= ", "+tableName;
		}
	}
}
