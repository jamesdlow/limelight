package com.jameslow.limelight;

import java.util.*;

public class FilterList {

	public static List<String> Filter(List<String> list, String filter) {
		return Filter(list,filter,false);
	}
	public static List<String> Filter(List<String> list, String filter, boolean casesensitive) {
		return Filter(list,filter,false,false);
	}
	public static List<String> Filter(List<String> list, String filter, boolean casesensitive, boolean fulltext) {
		List<String> filtered = new ArrayList<String>();
				
		if (filter.compareTo("") != 0 || filter.length() > 0) {
			if (!casesensitive) {
				filter = filter.toUpperCase();
			}

			for(String item : list) {
				String tempitem;
				if (casesensitive) {
					tempitem = item;
				} else {
					tempitem = item.toUpperCase();
				}
				if (fulltext) {
					if (match(filter,tempitem)) {
						filtered.add(item);
					}					
				} else {
					if (tempitem.indexOf(filter) >= 0) {
						filtered.add(item);
					}
				}
			}
		} else {
			filtered = list;
		}
		return filtered;
	}
	
	public static boolean match(String needle, String haystack) {
		String regex = " |,|\\.|;|:|\t";
		String[] needles = needle.split(regex);
		
		for (int i = 0; i < needles.length; i++ ) {
			if (!(haystack.indexOf(needles[i]) >= 0)) {
				return false;
			}
		}
		return true;
	}
}
