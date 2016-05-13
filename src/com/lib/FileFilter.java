package com.lib;

import java.io.File;

public class FileFilter {

   public static boolean accept(File file, String fileType) {
	   
	   if(fileType!=null && !fileType.equalsIgnoreCase("")){
		   String[] splitFileType = fileType.split(",");
		   for(String fType : splitFileType){
			   fType = fType.replace("*", "");
			   if(file.getName().toLowerCase().endsWith(fType)){
				   return true;
			   }
		   }
	   }
      return false;
   }
}