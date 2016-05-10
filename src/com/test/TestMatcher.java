package com.test;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lib.FileSearchProcessor;
import com.lib.Searcher;

public class TestMatcher {
	private static final String REGEX = "^internalName";
    private static final String INPUT = "cat cat2";
    private static Pattern pattern;
    private static Matcher matcher =null;

    public static void main( String args[] ){
	   //(?!.*<!--)companyName(?!.*-->)
	   //String regex = "(?<=--%>).*";(?=<%--)
	   //String regex = "companyNam(?=<%--.*--%>)";
	   //String regex = ".*companyName3|(?!\\s*<%--)&(?!\\s*<!--)companyName3(?!\\s*--%>)&(?!\\s*-->)";
	   //String regex = "companyName(?=\\s<%--.*--%>)|(?<=\\--%>)\\s(companyName)";
    	String regex1 = "\\/\\*([^*])*\\*+\\/";
       //String regex = "(\\/\\*)(\\*/)|^[<%--](--%>)|(<!--)(-->)"; --><%-- --%>
    	String regex = ".*companyName.*(?=\\s<%--.*--%>)|.*companyName.*(?=\\s<%--)|(?<=\\--%>)\\s.*(companyName)|.*companyName.*(?=\\s<!--.*-->)" +
    			"|.*companyName.*(?=\\s<!--)|(?<=\\-->)\\s.*(companyName)|companyName.*(?=/\\*.*\\*/)|.*companyName.*(?=\\s//*)" +
    			"|(?<=\\*/).*companyName" +
    			"|.*companyName.*(?=\\s//)|(?!.*<%--)companyName(?!.*--%>)";
    	
	   String aa ="--%> companyName";
	   pattern = Pattern.compile(regex1,Pattern.CASE_INSENSITIVE);
	   matcher = pattern.matcher(aa);
	   search();
	   
	   if ( matcher.find() ) {
		   System.out.println("matcher "+matcher.group(0));
		}
	   else{
		   System.out.println("No match ");
	   }
   }
    
    public static void search(){
    	String dataDirPath = "C:\\Users\\Nvizion\\Downloads\\data\\New folder\\New folder";
    	File[] files = new File(dataDirPath).listFiles();
    	Searcher searcher = new Searcher();
    	String keyword = "companyName";
    	FileSearchProcessor searchProcessor = new FileSearchProcessor(dataDirPath, "*.jsp");
    	
        for (File file : files) {
      		 if(!file.isHidden()
      		            && file.exists()
      		            && file.canRead()
      		         ){
      		            searcher.search(file,keyword,searchProcessor); 
      		         }
      	 }
        }
}
