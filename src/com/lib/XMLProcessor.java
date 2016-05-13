package com.lib;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import constants.SearchConstants;

public class XMLProcessor {
	
	private static final String DERIVATION = "derivation";
	private static final String EXPRESSION = "expression";
	private String xmlFilePath;
	private String derivedProperties;
	
	public String getDerivedProperties() {
		return derivedProperties;
	}

	public void setDerivedProperties(String derivedProperties) {
		this.derivedProperties = derivedProperties;
	}
	public XMLProcessor(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
		this.derivedProperties = SearchConstants.EMPTY_STRING;
	}

	@SuppressWarnings("unchecked")
	public List<com.SearchRecord> processXML(String filesDir, String filesTypes) throws IOException {
		
		File inputFile = new File(xmlFilePath);
		SAXReader saxReader = new SAXReader();
		SearchProcessor searchProcessor = new SearchProcessor(filesDir, filesTypes);
		
		try {
			Document document = saxReader.read(inputFile);
			
			List<Node> selectNodes = document.selectNodes(SearchConstants.ROOT_NODE);
			for (Node node : selectNodes) {
				Element itemDescriptorElement = (Element)node;
				String itemName = itemDescriptorElement.attributeValue(SearchConstants.NAME);
				System.out.println("Item descriptor ==========================="+itemName);
				List<Object> elements = itemDescriptorElement.elements();
				Element propertyElement = null;
				for (Object object : elements) {
					Element element = (Element)object;
					if(element.getName().equalsIgnoreCase(SearchConstants.TABLE)){
						String tableName = element.attributeValue(SearchConstants.NAME);
						if(!tableName.startsWith("dcs")){
							List<Object> properties = element.elements();
							for (Object property : properties) {
								propertyElement = (Element)property;
								initiateSearch(propertyElement,itemName,searchProcessor,tableName);
							}
						}
					}
					else if(element.getName().equalsIgnoreCase("property")){
						propertyElement = (Element)element;
						initiateSearch(propertyElement,itemName,searchProcessor,SearchConstants.EMPTY_STRING);
					}
				}
			}
			
			System.out.println(SearchConstants.COMPLETED);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return searchProcessor.getDocuments();
	}
	
	public void initiateSearch(Element propertyElement, String itemName, SearchProcessor searchProcessor, String tableName){
		List<Object> propertyElements = propertyElement.elements();
		
		String derivedValue = checkForDerivedProperty(propertyElements);
		if(derivedValue != null){
			populateDerivedProperty(derivedValue);
		}
		String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
		String columnName = propertyElement.attributeValue(SearchConstants.COLUMN_NAME);
		searchProcessor.processSearch(propertyName,itemName,columnName,tableName);
		searchProcessor.resetFilesData();
	}

	public String checkForDerivedProperty(List<Object> propertyElements) {
		
		String derivedValue = null;
		for(Object element : propertyElements){
			
			Element childElement = (Element)element;
			String temp = childElement.getName();
			if(childElement.getName().equalsIgnoreCase(DERIVATION)){
				List<Object> derivationElements = childElement.elements();
				
				for(Object derivationElement : derivationElements){
					Element dElement = (Element)derivationElement;
					if(dElement.getName().equalsIgnoreCase(EXPRESSION)){
						derivedValue = dElement.getStringValue();
					}
				}
			}
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

}
