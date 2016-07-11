package com.nviz;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import constants.SearchConstants;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MultiTypeTables {

	public static void main(String[] args) throws IOException {
		
		File inputFile = null;
		String csvFileName = null;
		if(args.length < 2){
			usage();
			return;
		} else {
			inputFile = new File(args[0]);
			csvFileName = args[1];
		}
		generateMultiTablesData(inputFile, csvFileName);
	}

	@SuppressWarnings("unchecked")
	private static void generateMultiTablesData(File inputFile, String csvFileName) {
		
		try {
			List<String[]> headerList = new ArrayList<String[]>();
			String[] headerArray = {"TABLE", "COLUMN_NAME", "CHILD_COLUMN_NAME"};
			headerList.add(headerArray);
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(inputFile);

			List<Node> selectNodes = document
					.selectNodes("gsa-template/item-descriptor");
			Set<String[]> tableDataSet = new HashSet<String[]>();
			for (Node node : selectNodes) {
				Element itemDescriptorElement = (Element) node;
				
				List<Object> elements = itemDescriptorElement.elements();
				
				for (Object object : elements) {
					Element element = (Element) object;
					String elementName = element.getName();
					if (elementName != null && elementName.equalsIgnoreCase("table")) {
						
						String tableType = element.attributeValue("type");
						if (tableType != null && tableType.equalsIgnoreCase("multi")) {
							
							String metaData [] = new String[3];
							metaData[0] = element.attributeValue("name");
							if(element.attributeValue("id-column-name") != null)
								metaData[1] = element.attributeValue("id-column-name");
							else
								metaData[1] = element.attributeValue("id-column-names");
							metaData[2] = getChildColumnName(element);
							
							if(metaData[2] != null && !metaData[2].equalsIgnoreCase(""))
								tableDataSet.add(metaData);
						}
					}
				}
			}
			System.out.println("Writing data to csv file..");
			CSVWriter writer = new CSVWriter(new FileWriter(csvFileName));
			System.out.println("csv file writting completed..");
			//writer.writeAll(headerList, false);
			List<String[]> tableDataList = new ArrayList<>(tableDataSet);
			writer.writeAll((List<String[]>) tableDataList, false);
			writer.close();
			System.out.println("Completed");
		} catch (DocumentException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static String getChildColumnName(Element element) {
		
		String childColumnName = null;
		List<Object> properties = element.elements();
		if(properties != null && properties.size() > 0){
			Element property = (Element) properties.get(0);
			if(property != null){
				childColumnName = property.attributeValue(SearchConstants.COLUMN_NAME);
				if(childColumnName == null)
					childColumnName = property.attributeValue(SearchConstants.NAME);
			}
		}
		return childColumnName;
	}
	
	private static void usage(){
		System.err.println("Usage: GetReferentialIntegrityDetails <repository definition file path> <database csv file name> <output  csv file path>");
		System.err.println("Example: GetReferentialIntegrityDetails D:/workspace/RegionRepository.xml D:/Plantronics/DB.csv D:/Plantronics/Reports/RegionRepositoryReport.csv");
		System.err.println("Do not enclose the file names with in double quotes or single quotes");
		System.err.println("Enter the file names with space separated");
	}
}
