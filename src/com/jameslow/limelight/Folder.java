package com.jameslow.limelight;

import java.awt.event.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jameslow.*;
import com.jameslow.gui.SearchTextFilter;

public class Folder extends PopupDroppableList implements SearchTextFilter {
	private java.util.List<String> files;
	private String path = "";
	
	public Folder(final LimelightWindow limelight) {
		super(limelight);
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
		files = getFileList();
		setListData(files.toArray());
	}
	private java.util.List<String> getFileList() {
		File dir = new File(path);
		String[] files = dir.list(FileUtils.getHiddenDirFilter());
		java.util.List<String> filelist = new ArrayList<String>();
		for (int i=0; i < files.length; i++) {
			filelist.add(files[i]);
		}
		return filelist;
	}
	public void setListData(Object[] listData) {
		removeAllFilepaths();
		for(int i=0; i < listData.length; i++) {
			String filename = (String)listData[i];
			addFilepath(path + Main.OS().fileSeparator() + filename,-1);
		}
	}
	public void doFilter(String text) {
		setListData(FilterList.Filter(files, text, false, true).toArray());
	}
}
