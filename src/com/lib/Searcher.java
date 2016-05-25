package com.lib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class Searcher {

	/*public void search(File file, String keyword, SearchProcessor searchProcessor){
		
		String regex = constructRegEx(keyword);
		Pattern pattern = Pattern.compile(regex);
		FileInputStream inputStream ;
		Pattern commentsPattern;
		Matcher matcher;
		String fileString = "";
		String commentsRegex = constructCommentsRegEx(file.getAbsolutePath());
		
		if(commentsRegex != null){
			try {
				inputStream = new FileInputStream(file);
				fileString = IOUtils.toString(inputStream, "UTF-8");
				commentsPattern = Pattern.compile(commentsRegex);
				matcher = commentsPattern.matcher(fileString);
				
				while(matcher.find()){
					fileString = fileString.replace(matcher.group(), "");
				}
				matcher = pattern.matcher(fileString);
				while(matcher.find()){
					searchProcessor.incrementHitsCount();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("File reading exception "+file.getAbsolutePath());
				e.printStackTrace();
			}
		}
		else{
			System.out.println("Did not find regex for matching comments pattern for file "+file.getAbsolutePath());
		}
		
	}*/

	public static String constructRegEx(String keyword, String fileName) {
		
		if((fileName == null) || (fileName.length() == 0) || (fileName.trim().length() == 0)){
			return null;
		}
		String regex = null;
		if(fileName.endsWith(".jspf")||fileName.endsWith(".jsp")){
			regex = "\\.\\b"+keyword+"\\b";
		}
		else{
			regex = "\\b"+keyword+"\\b";
		}
		
		//String regex = "\bcompanyName\b.*(?=//(.*))";
		return regex;
	}
	
	public static String constructCommentsRegEx(String fileName){
		if((fileName == null) || (fileName.length() == 0) || (fileName.trim().length() == 0)){
			return null;
		}
		String regex = null;
		
		if(fileName.endsWith(".java")){
			regex = "<%--(.|\\s)*?--%>|<!--(.|\\s)*?-->|\\/\\*(.|\\s)*?\\*/|//(.*)";
		}
		else if(fileName.endsWith(".jspf")||fileName.endsWith(".jsp")||fileName.endsWith(".txt")){
			regex = "<%--(.|\\s)*?--%>|<!--(.|\\s)*?-->";
		}
		else if(fileName.endsWith(".js")){
			regex = "\\/\\*(.|\\s)*?\\*/|//(.*)";
		}
		return regex;
	}
	
	
}
