package com;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import com.lib.RepositoryRecord;

import constants.SearchConstants;

/**
 * this class will list out the common properties from repositories
 * @author Srinivas
 *
 */
public class CommonPropertiesExtractor {
	
	private static final String SPACE = " ";
	private List<RepositoryRecord> recordsList = null;
	private String commonProperties = null;
	private String definitionFilesDir = null; //"E:\\Plantronics\\Catalog-cleanup\\repositories";
	
	public CommonPropertiesExtractor(String definitionFilesDir){
		this.recordsList = new ArrayList<RepositoryRecord>();
		this.commonProperties = "";
		this.definitionFilesDir = definitionFilesDir;
	}

	public String getCommonProperties() {
		
		extractProperties();
		findCommonProperties();
		return this.commonProperties;
	}

	private void findCommonProperties() {
		
		for(int i = 0; i < recordsList.size()-1; i++){
			String properties1 = recordsList.get(i).getProperties();
			for(int j = i+1; j < recordsList.size(); j++){
				String properties2 = recordsList.get(j).getProperties();
				performMatching(properties1, properties2);
			}
		}
	}

	private void performMatching(String properties1, String properties2) {
		
		StringTokenizer pTokens = new StringTokenizer(properties1,SPACE);

		while(pTokens.hasMoreTokens()){
			String cProperty = pTokens.nextToken();
			String regex = "\\b"+cProperty+"\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(properties2);
			while(matcher.find()){
				addCommonProperty(cProperty);
			}
		}
	}

	private void addCommonProperty(String cProperty) {
		
		if(!commonProperties.contains(cProperty)){
			this.commonProperties += cProperty+SPACE;
		}
	}

	@SuppressWarnings("unchecked")
	public void extractProperties() {
		File[] files = new File(definitionFilesDir).listFiles();

	      for (File file : files) {
	    		 if(!file.isHidden() && !file.isDirectory()){
	    				SAXReader saxReader = new SAXReader();
	    				RepositoryRecord record = new RepositoryRecord(file.getName());
	    				String properties = "";
	    				
	    				try {
	    					Document document = saxReader.read(file);
	    					
	    					List<Node> selectNodes = document.selectNodes(SearchConstants.ROOT_NODE);
	    					for (Node node : selectNodes) {
	    						Element itemDescriptorElement = (Element)node;
	    						List<Object> elements = itemDescriptorElement.elements();
	    						
	    						for (Object object : elements) {
	    							Element element = (Element)object;
	    							
	    							if(element.getName().equalsIgnoreCase(SearchConstants.PROPERTY)){
	    								Element propertyElement = (Element)element;
	    								String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
	    								properties += propertyName+SPACE;
	    							}
	    							else if(element.getName().equalsIgnoreCase(SearchConstants.TABLE)){
	    								List<Object> allProperties = element.elements();
	    								
	    								for (Object property : allProperties) {
	    									Element propertyElement = (Element)property;
	    									String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
	    									properties += propertyName+SPACE;
	    									}
	    								}
	    							}
	    					}
	    				} catch (DocumentException e) {
	    					e.printStackTrace();
	    				}
	    				record.setProperties(properties);
	    				recordsList.add(record);
	    		 }
	      }
	}
}

