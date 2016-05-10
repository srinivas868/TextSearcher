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
	
	private static final String DEFAULT = "Default";
	private String xmlFilePath;

	public XMLProcessor(String xmlFilePath) {
		this.xmlFilePath = xmlFilePath;
	}

	@SuppressWarnings("unchecked")
	public List<com.SearchRecord> processXML(String filesDir, String filesTypes) throws IOException {
		
		File inputFile = new File(xmlFilePath);
		SAXReader saxReader = new SAXReader();
		FileSearchProcessor searchProcessor = new FileSearchProcessor(filesDir, filesTypes);
		
		try {
			Document document = saxReader.read(inputFile);
			Element rootElement = document.getRootElement();
			
			List<Node> selectNodes = document.selectNodes(SearchConstants.ROOT_NODE);
			for (Node node : selectNodes) {
				Element itemDescriptorElement = (Element)node;
				String itemName = itemDescriptorElement.attributeValue(SearchConstants.NAME);
				System.out.println("Item descriptor ==========================="+itemName);
				List<Object> elements = itemDescriptorElement.elements();
				
				for (Object object : elements) {
					Element element = (Element)object;
					if(element.getName().equalsIgnoreCase(SearchConstants.TABLE)){
						
						List<Object> properties = element.elements();
						for (Object property : properties) {
							Element propertyElement = (Element)property;
							String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
							List<Object> propertyElements = propertyElement.elements();
							
							//boolean isDerived = checkForDerivedProperty(propertyElements);
							if(!propertyName.endsWith(DEFAULT)){
								String columnName = propertyElement.attributeValue(SearchConstants.COLUMN_NAME);
								searchProcessor.processSearch(propertyName,itemName,columnName);
								searchProcessor.resetHitsCount();
							}
						}
					}
				}
			}
			
			System.out.println(SearchConstants.COMPLETED);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return searchProcessor.getDocuments();
	}

	public boolean checkForDerivedProperty(List<Object> propertyElements) {
		
		for(Object element : propertyElements){
			Element childElement = (Element)element;
			childElement.getName();
			if(childElement.getName().equalsIgnoreCase(("derivation"))){
				return true;
			}
		}
		return false;
	}

	public void processDocuments() {
		
	}

}
