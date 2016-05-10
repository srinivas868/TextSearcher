package com.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Searcher {

	Matcher matcher;
	Pattern pattern;
	BufferedReader br;
	boolean isComment;
	
	public void search(File file, String keyword, FileSearchProcessor textSearcher){
		//System.out.println("Indexing : "+file.getAbsolutePath());
		String regex = constructRegEx(keyword);
		pattern = Pattern.compile(regex);
		FileInputStream inputStream ;
		String fileString = "";
		Matcher matcher = null;
		
		try {
			inputStream = new FileInputStream(file);
			fileString = IOUtils.toString(inputStream, "UTF-8");
			fileString = fileString.replaceAll("<%--(.|\\s)*?--%>|<!--(.|\\s)*?-->|\\/\\*(.|\\s)*?\\*/", "");
			matcher = pattern.matcher(fileString);
			while(matcher.find()){
				System.out.println("Value --> "+matcher.group(0));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean checkForComments(String line) {
		
		String commentsRegEx = "(?=.*<%--)(?!.*--%>).*|(?=.*<!--)(?!.*-->).*|(?=.*\\/\\*)(?!.*\\*/).*|" +
    			"^(?!.*<%--)(?=.*--%>).*|^(?!.*<!--)(?=.*-->).*|^(?!.*\\/\\*)(?=.*\\*/).*";
		String endCommentRegEx = "(\\*/)|(--%>)|(-->)";
		Pattern commentsPattern = Pattern.compile(commentsRegEx,Pattern.CASE_INSENSITIVE);
		Matcher commentsMatcher = null;
		commentsMatcher = commentsPattern.matcher(line);
		if(commentsMatcher.find()){
			if(!isComment){
				isComment = true;
			}
			else{
				isComment = false;
			}
		}
		
		if(commentsMatcher.find()){
			return true;
		}
		return false;
	}

	private String constructRegEx(String keyword) {
		String regex1 = "/*([^*])\\*\\*+/";
		String regex2 = "\\b"+keyword+"\\b";
		String regex = ".*"+keyword+".*(?=\\s<%--.*--%>)|.*"+keyword+".*(?=\\s<%--)|(?<=\\--%>)\\s.*("+keyword+")|.*" +
				keyword+".*(?=\\s<!--.*-->)" +
    			"|.*"+keyword+".*(?=\\s<!--)|(?<=\\-->)\\s.*("+keyword+")|"+keyword+".*(?=/\\*.*\\*/)|.*"+keyword+".*(?=\\s//*)" +
    			"|(?<=\\*/).*"+keyword+"|.*"+keyword+".*(?=\\s//)";
		return regex2;
	}
}
