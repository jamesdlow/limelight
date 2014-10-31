package com.jameslow.limelight;

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;

import com.jameslow.*;
import com.jameslow.gui.SearchText;
import com.jameslow.gui.SearchTextFilter;

public class Search extends AbstractWindow implements SearchTextFilter, Runnable {
	private LimelightWindow window;
	private Thread thread;
	private PopupDroppableList folder;
	private SearchText searchtext;
	public boolean run = true;
	
	public Search (LimelightWindow window) {
		this.window = window;
	}
	public Search(LimelightWindow window, JMenuBar menu) {
		this(window);
		setJMenuBar(menu);
		setLayout(new BorderLayout());
		searchtext = new SearchText(false, this);
		add(searchtext, BorderLayout.NORTH);
		folder = new PopupDroppableList(window);
		folder.getComponent().addMouseListener(window.mainpanel);
		JScrollPane scroll= new JScrollPane(folder.getComponent(),JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
	}
	public void postLoad() {}
	public PopupDroppableList getFileList() {
		return folder;
	}
	public String getDefaultTitle() {
		return "Search";
	}
	public WindowSettings getDefaultWindowSettings() {
		return new WindowSettings(300,400,100,100,false,JFrame.NORMAL);
	}
	public void doFilter(String text) {
		run = false;
		if (thread != null) {
			thread = null;
		}
		if (text.trim().compareTo("") != 0) {
			folder.removeAllFilepaths();
			thread = new Thread(this);
			run = true;
			thread.start();
		}
	}
	public void run() {
		Map<String,String> folders = window.settings.Folders;
		String searchterm = searchtext.getText().trim().toLowerCase();
		int i = 0;
		Object[] keys = folders.keySet().toArray();
		while (run && i < folders.size()) {		
			File dir = new File(folders.get((String) keys[i]));
			if (dir.exists() && dir.isDirectory()) {
				int j = 0;
				String[] files = dir.list(FileUtils.getHiddenDirFilter());
				while (run && j < files.length) {
					File file = new File(dir.getAbsolutePath() + Main.OS().fileSeparator() + files[j]);
					if (file.exists() && !file.isDirectory()) {
						String filepath = file.getAbsolutePath();
						if (FileUtils.getFilename(filepath).toLowerCase().indexOf(searchterm) >= 0) {
							folder.addFilepath(filepath);
						} else if (FileUtils.getExt(file.getAbsolutePath()).toLowerCase().compareTo("txt") == 0) {
							//TODO: For now only search text files, but eventually include xml and document files
							try {
								BufferedReader input = new BufferedReader(new FileReader(file));
								String line;
								boolean notfound = true;
								while (run && notfound && (line = input.readLine()) != null){
									if (line.toLowerCase().indexOf(searchterm) >= 0) {
										folder.addFilepath(filepath);
										notfound = false;
									}
								}
							} catch (IOException e) {
								Main.Logger().warning("Cannot load text file: " + file);
							}
						}
					}
					j++;
				}
			}
			i++;
		}
	}
}
