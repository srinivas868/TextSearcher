package com;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import au.com.bytecode.opencsv.CSVWriter;
public class GetDefinitionFile {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		File inputFile = new File("C:/Users/Nvizion/Downloads/data/customCatalog.xml");
		String csvFileName = "catalog.csv";
		SAXReader saxReader = new SAXReader();
		List<String[]> headerList = new ArrayList<String[]>();
		//define the header in CSV file
		String[] headerArray = {"ItemDescriptor","Table", "TableType", "Id-Column-Name"};
		headerList.add(headerArray);
		try {
			//Read the repository definiton file
			Document document = saxReader.read(inputFile);
			
			//get all the Item-descriptors from the repository definition file
			List<Node> selectNodes = document.selectNodes("gsa-template/item-descriptor");
			//Iterate all the Item descriptors
			List<String[]> repostioryDetailsList = new ArrayList<String[]>();
			for (Node node : selectNodes) {
				List<String> itemDescriptorList = new ArrayList<String>();
				Element itemDescriptorElement = (Element)node;
				itemDescriptorList.add(itemDescriptorElement.attributeValue("name"));
				repostioryDetailsList.add(itemDescriptorList.toArray(new String[itemDescriptorList.size()]));
				// list all the  tags inside the item descriptor
				List<Object> elements = itemDescriptorElement.elements();
				for (Object object : elements) {
					Element element = (Element)object;
					if(element.getName().equalsIgnoreCase("table")){
						List<String> detailsList = new ArrayList<String>();
						detailsList.add("");
						detailsList.add(element.attributeValue("name"));
						detailsList.add(element.attributeValue("type"));
						detailsList.add(element.attributeValue("id-column-name"));
						repostioryDetailsList.add(detailsList.toArray(new String[detailsList.size()]));
					}
				}
			}
			CSVWriter writer = new CSVWriter(new FileWriter(csvFileName));
			writer.writeNext(headerArray);
			//writer.writeAll(headerList,false);
			//writer.writeAll(repostioryDetailsList,false);
			writer.close();
		} catch (DocumentException e) {
			System.out.println(e);
		}
	}

}
