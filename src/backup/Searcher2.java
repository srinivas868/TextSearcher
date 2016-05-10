package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Searcher {

	Matcher matcher;
	Pattern pattern;
	BufferedReader br;
	File file;
	int lineCount=1;
	TextSearcher textSearcher;
	boolean commentFound;
	
	public void search(File pFile, String keyword, TextSearcher pTextSearcher){
		
		//String regex = constructRegEx(keyword);
		boolean isComment =false;
		String regex = "[^A-Za-z0-9]";
		pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		String line = "";
		file = pFile;
		textSearcher = pTextSearcher;
		System.out.println("Indexing : "+file.getAbsolutePath());
		
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) 
			{
				//int cmtStartIndex = line.indexOf(startComment);
				//int cmtEndIndex = line.indexOf(endComment);
				matcher = pattern.matcher(line);
				if(matcher.find()){
					String constructedLine = checkForComments(line.replaceAll("^\\s+", ""));
					if(constructedLine != null){
						isComment = commentFound;
						if(constructedLine.contains(keyword)){
							addDocument();
						}
					}
					else{
						if(!isComment && line.contains(keyword)){
							addDocument();
						}
					}
				}
				else if(!isComment && line.contains(keyword)){
						addDocument();
					}
				
				lineCount++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addDocument() {
		Document doc = new Document(file,lineCount);
		textSearcher.addDocument(doc);
		textSearcher.incrementHitsCount();
	}

	private String checkForComments(String line) {
		int index;
		String constructedLine=line;
		
		if((index = line.indexOf("<!--")) != -1){
			commentFound = true;
			return line.substring(0, index);
		}
		else if((index = line.indexOf("<%--")) != -1){
			commentFound = true;
			return line.substring(0, index);
		}
		else if((index = line.indexOf("-->")) != -1){
			commentFound = false;
			return line.substring(index+3);
		}
		else if((index = line.indexOf("--%>")) != -1){
			commentFound = false;
			return line.substring(index+4);
		}
		else if((index = line.indexOf("//")) != -1){
			commentFound = false;
			return line.substring(0, index);
		}
		else if((index = line.indexOf("/*")) != -1){
			commentFound = true;
			return line.substring(index);
		}
		else if((index = line.indexOf("*/")) != -1){
			commentFound = false;
			return line.substring(index+2);
		}
		
		return null;
	}

	private String constructRegEx(String keyword) {
		String reg = "(?!\\s*<%--)"+keyword+"(?!\\s*--%>)";
		return reg;
	}
}
