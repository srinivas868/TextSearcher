package com.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class Searcher {

	Matcher matcher;
	Pattern pattern;
	Pattern commentsPattern;
	boolean isComment;
	
	public void search(File file, String keyword, SearchProcessor searchProcessor){
		String regex = constructRegEx(keyword);
		pattern = Pattern.compile(regex);
		FileInputStream inputStream ;
		String fileString = "";
		Matcher matcher = null;
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
		
	}

	private String constructRegEx(String keyword) {
		String regex2 = "\\b"+keyword+"\\b";
		String regex = "\bcompanyName\b.*(?=//(.*))";
		return regex2;
	}
	
	private String constructCommentsRegEx(String fileType){
		if((fileType == null) || (fileType.length() == 0) || (fileType.trim().length() == 0)){
			return null;
		}
		String regex = null;
		
		if(fileType.endsWith(".java")){
			regex = "<%--(.|\\s)*?--%>|<!--(.|\\s)*?-->|\\/\\*(.|\\s)*?\\*/|//(.*)";
		}
		else if(fileType.endsWith(".jspf")||fileType.endsWith(".jsp")||fileType.endsWith(".txt")){
			regex = "<%--(.|\\s)*?--%>|<!--(.|\\s)*?-->";
		}
		else if(fileType.endsWith(".js")){
			regex = "\\/\\*(.|\\s)*?\\*/|//(.*)";
		}
		return regex;
	}
	
	private String getFileExtension(File file){
		String extension = null;

		int i = file.getAbsolutePath().lastIndexOf('.');
		if (i >= 0) {
		    extension = file.getAbsolutePath().substring(i+1);
		}
		
		return extension;
	}
}
