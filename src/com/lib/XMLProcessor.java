package com.lib;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.CommonPropertiesExtractor;
import com.lib.SearchRecord;

import constants.SearchConstants;

public class XMLProcessor {
	
	private List<SearchRecord> recordsList;
	private String xmlFilePath;
	private String derivedProperties;
	private String definitionFilesDir = null;
	
	public String getDerivedProperties() {
		return derivedProperties;
	}

	public void setDerivedProperties(String derivedProperties) {
		this.derivedProperties = derivedProperties;
	}
	public XMLProcessor(String xmlFilePath, String definitionFilesDir) {
		this.xmlFilePath = xmlFilePath;
		this.derivedProperties = SearchConstants.EMPTY_STRING;
		this.recordsList = new ArrayList<SearchRecord>();
		this.definitionFilesDir = definitionFilesDir;
	}

	@SuppressWarnings("unchecked")
	public void processXML(String filesDir, String filesTypes) throws IOException {
		
		File inputFile = new File(xmlFilePath);
		SAXReader saxReader = new SAXReader();
		CommonPropertiesExtractor extractor = new CommonPropertiesExtractor(definitionFilesDir);
		String commonProperties = extractor.getCommonProperties();
		
		try {
			Document document = saxReader.read(inputFile);
			ExecutorService executor = Executors.newFixedThreadPool(200);
			
			List<Node> selectNodes = document.selectNodes(SearchConstants.ROOT_NODE);
			for (Node node : selectNodes) {
				Element itemDescriptorElement = (Element)node;
				String itemName = itemDescriptorElement.attributeValue(SearchConstants.NAME);
				System.out.println("Item descriptor ==========================="+itemName);
				List<Object> elements = itemDescriptorElement.elements();
				List<Object> tableElements = new ArrayList<Object>();							//store table element
				
				//loop to iterate transient properties
				for (Object object : elements) {
					Element element = (Element)object;
					
					if(element.getName().equalsIgnoreCase(SearchConstants.PROPERTY)){
						Element propertyElement = (Element)element;
						List<Object> propertyElements = propertyElement.elements();
						String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
						String columnName = propertyElement.attributeValue(SearchConstants.COLUMN_NAME);
						checkForDerivedProperty(propertyElements);
						Runnable searchProcessor = new SearchProcessor(filesDir, filesTypes, propertyName, 
															itemName, columnName, SearchConstants.EMPTY_STRING, 
															commonProperties, this);
						executor.execute(searchProcessor);
					}
					else if(element.getName().equalsIgnoreCase(SearchConstants.TABLE)){
						tableElements.add(object);
					}
				}
				
				//loop to iterate table properties
				for (Object object : tableElements) {
					Element element = (Element)object;
					if(element.getName().equalsIgnoreCase(SearchConstants.TABLE)){
						List<Object> properties = element.elements();
						String tableName = element.attributeValue(SearchConstants.NAME);
						
						for (Object property : properties) {
							Element propertyElement = (Element)property;
							String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
							String columnName = propertyElement.attributeValue(SearchConstants.COLUMN_NAME);
							
							if(!derivedProperties.contains(propertyName) && !tableName.startsWith(SearchConstants.DCS)){
								Runnable searchProcessor = new SearchProcessor(filesDir, filesTypes, propertyName, 
																	itemName, columnName, tableName, 
																	commonProperties, this);
								executor.execute(searchProcessor);
							}
						}
					}
				}
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				//System.out.println("waiting --->");
	        }
			
			System.out.println(SearchConstants.COMPLETED);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public String checkForDerivedProperty(List<Object> propertyElements) {
		
		String derivedValue = null;
		for(Object element : propertyElements){
			
			Element childElement = (Element)element;
			if(childElement.getName().equalsIgnoreCase(SearchConstants.DERIVATION)){
				List<Object> derivationElements = childElement.elements();
				
				for(Object derivationElement : derivationElements){
					Element dElement = (Element)derivationElement;
					if(dElement.getName().equalsIgnoreCase(SearchConstants.EXPRESSION)){
						derivedValue = dElement.getStringValue();
					}
				}
			}
		}
		if(derivedValue != null){
			populateDerivedProperty(derivedValue);
		}
		return derivedValue;
	}

	public void populateDerivedProperty(String propertyName) {
		
		if(getDerivedProperties().equalsIgnoreCase(SearchConstants.EMPTY_STRING)){
			setDerivedProperties(propertyName);
		}
		else{
			setDerivedProperties(getDerivedProperties()+","+propertyName);
		}
	}
	
	public void addRecord(SearchRecord doc){
		synchronized (recordsList) {
			recordsList.add(doc);
		}
	}
	
	public List<SearchRecord> getDocuments(){
		return recordsList;
	}

}
