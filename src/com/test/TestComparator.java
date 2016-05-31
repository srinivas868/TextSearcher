package com.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.lib.SearchRecord;

public class TestComparator {

	public static void main(String[] args) {
		SearchRecord record1 = new SearchRecord("article", "propertyName1");
		SearchRecord record2 = new SearchRecord("folder", "propertyName1");
		SearchRecord record3 = new SearchRecord("article", "propertyName2");
		List<SearchRecord> list = new ArrayList<SearchRecord>(); 
		list.add(record1);
		list.add(record2);
		list.add(record3);
		Collections.sort(list, new NameComparator());
		System.out.println("Done "+list);
	}
}
	class NameComparator implements Comparator<SearchRecord>{

		@Override
		public int compare(SearchRecord o1, SearchRecord o2) {
			return o1.getItemDescriptorName()
					.compareToIgnoreCase(o2.getItemDescriptorName());
		}
		
	}

