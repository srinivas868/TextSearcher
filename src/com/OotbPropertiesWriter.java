package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import com.csvreader.CsvWriter;
import constants.SearchConstants;

/**
 * this class will list out the OOTB properties declared as transient
 * @author Nvizion
 *
 */
public class OotbPropertiesWriter {

	private static final String PROPERTY = "property";
	private List<SearchRecord> recordsList = new ArrayList<SearchRecord>();
	private String[] headerArray = {"ITEM_DESCRIPTOR", "PROPERTY"};
	private String csvDir = "E:\\Nviz\\CSV_files";
	private String csvFileName = "Claimable_OOTB_Properties.csv";
	
	public static void main(String[] args) {
		String xmlFilePath = "E:\\Nviz\\CSV_files\\claimableRepository.xml";
		OotbPropertiesWriter writer = new OotbPropertiesWriter();
		try {
			System.out.println("Reading XML file -----------> "+xmlFilePath);
			writer.processXML(xmlFilePath);
			System.out.println("Completed Reading ---------------> "+xmlFilePath);
			System.out.println();
			System.out.println("Started writing data to CSV "+writer.csvDir+"\\"+writer.csvFileName);
			writer.startWritingTOCsv();
			System.out.println("Completed writing data to CSV ");
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
				Element propertyElement = null;
				for (Object object : elements) {
					Element element = (Element)object;
						//consider only transient properties
						if(element.getName().equalsIgnoreCase(PROPERTY)){
							propertyElement = (Element)element;
							String propertyName = propertyElement.attributeValue(SearchConstants.NAME);
							SearchRecord doc = new SearchRecord(itemName, propertyName);
							addRecord(doc);
						}
				}
			}
			
			System.out.println(SearchConstants.COMPLETED);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public void addRecord(SearchRecord doc){
		recordsList.add(doc);
	}
	
	public void startWritingTOCsv() throws IOException{
		
		CsvWriter writer = new CsvWriter(new FileWriter(csvDir+"\\"+csvFileName, true), ',');
		writer.writeRecord(headerArray);
		
		for(SearchRecord record : recordsList){
			
			writer.write(record.getItemDescriptorName());
			writer.write(record.getPropertyName());
			writer.endRecord();
		}
		writer.close();
	}
}
