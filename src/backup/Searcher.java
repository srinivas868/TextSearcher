package backup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Document;

public class Searcher {

	Matcher matcher;
	Pattern pattern;
	BufferedReader br;
	
	public void search(File file, String keyword, TextSearcher textSearcher){
		System.out.println("Indexing : "+file.getAbsolutePath());
		//String regex = constructRegEx(keyword);
		String regex = "\\/\\*[^/*]*(?:(?!\\/\\*|\\*\\/)[/*][^/*]*)*\\*\\/";
		pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
		String line = "";
		int lineCount =1;
		boolean commentFound =false;
		
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) 
			{
				matcher = pattern.matcher(line);
				if(matcher.find()){
					//System.out.println("Matcher " +matcher.start());
					Document doc = new Document(file,lineCount);
					textSearcher.addDocument(doc);
					textSearcher.incrementHitsCount();
				}
				lineCount++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String constructRegEx(String keyword) {
		String reg = "(?!.*<!--)"+keyword+"(?!.*-->)";
		return reg;
	}
}
