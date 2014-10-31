package com.jameslow.limelight.handlers;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jameslow.*;
import com.jameslow.limelight.*;

public class TextItemHandler extends LimelightItemHandler {
	//TODO: fix this so it works for CRLF, LF, CR
	//Instead of this do:
	//1) replace CRLF with LF, CR with LF
	//2) Read through line by line, might not have to do (1) cause can use string reader
	//3) Group when there is a blank line through trim
	
	public TextItemHandler(LimelightWindow window) {
		super(window,LimelightSettings.EXT_TEXT);
	}

	public ComponentListener getComponentListener() {
		// TODO Auto-generated method stub
		return null;
	}

	public ListCellRenderer getListCellRenderer() {
		return new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				ItemSlide slide = (ItemSlide)value; 
				JTextArea textarea = new JTextArea((String) slide.getContent());
				textarea.setWrapStyleWord(true);
				if (isSelected) {
					textarea.setBackground(list.getSelectionBackground());
					textarea.setForeground(list.getSelectionForeground());
				}
				ListCellRenderer rend = new DefaultListCellRenderer();
				Component comp = rend.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (comp instanceof JComponent) {
					textarea.setBorder(((JComponent) comp).getBorder());
				}
				return textarea;
			}
		};
	}

	public boolean handlesDirectories() {
		return false;
	}

	public boolean isSinglePage(String filepath) {
		//TODO: Could override for txt files that will fix on a single page
		return false;
	}

	public ItemSlide[] getItemData(File file) {
		StringBuffer contents = new StringBuffer();
		BufferedReader input;
		String[] strings;
		try {
			input = new BufferedReader(new FileReader(file));
			String line; //not declared within while loop
			while (( line = input.readLine()) != null){
				contents.append(line);
				contents.append(Main.OS().lineSeparator());
			}
		} catch (IOException e) {
			Main.Logger().warning("Cannot load text file: " + file);
		}
		if (settings.DisplayLines <= 0 || settings.DisplayMethod == LimelightSettings.DisplayMode.ALL) {
			strings = getVerses(contents.toString());
		} else if (settings.DisplayMethod == LimelightSettings.DisplayMode.ABSOLUTE) {
			strings = splitTextAbsolute(contents.toString());
		} else {
			strings = splitTextEven(contents.toString());
		}
		ItemSlide[] items = new ItemSlide[strings.length];
		for(int i = 0; i<items.length; i++) {
			items[i] = new ItemSlide(file.getAbsolutePath(),i,strings[i]);
		}
		return items;
	}
	private String[] getLines(String text) {
		//Replace windows first, then just carriage returns incase mac
		return text.replaceAll("\r\n","").replaceAll("\r","\n").split("\n");
	}
	private String[] getVerses(String text) {
		String[] lines = getLines(text);
		ArrayList<String> verses = new ArrayList<String>();
		String currentverse = "";
		for (String line : lines) {
			if (line.trim().length() > 0) {
				if (currentverse.length() > 0) {
					currentverse = currentverse+"\n"+line;
				} else {
					currentverse = line;
				}
			} else if (currentverse.length() > 0) {
				//Only create new verse if we haven't just added one
				verses.add(currentverse);
				currentverse = "";
			}
		}
		//Add last line if its there
		if (currentverse.length() > 0) {
			verses.add(currentverse);
		}
		
		//Prepare for return
		String[] result = new String[verses.size()];
		for(int i = 0; i < verses.size(); i++) {
			result[i] = verses.get(i);
		}
		return result;
	}
	private String[] splitTextEven(String text) {
		String[] verses = getVerses(text);
		ArrayList<String> newslides = new ArrayList<String>();
		for (int i = 0; i < verses.length; i++) {
			String[] lines = verses[i].split(Main.OS().lineSeparator());
			int sections = 1;
			while ((lines.length / sections) > settings.DisplayLines) {
				sections++;
			}
			int linesused = lines.length / sections;
			int j = 0;
			while (j < lines.length) {
				int k = 0;
				StringBuffer slide = new StringBuffer();
				while (k < linesused && j < lines.length) {
					slide.append(lines[j]);
					slide.append(Main.OS().lineSeparator());
					k++;
					j++;
				}
				newslides.add(slide.toString());
			}
		}
		String[] result = new String[newslides.size()];
		for(int i = 0; i < newslides.size(); i++) {
			result[i] = newslides.get(i);
		}
		return result;	
	}
	private String[] splitTextAbsolute(String text) {
		String[] verses = getVerses(text);
		java.util.List<String> newslides = new ArrayList<String>();
		for (int i = 0; i < verses.length; i++) {
			String[] lines = verses[i].split(Main.OS().lineSeparator());
			int j = 0;
			while (j < lines.length) {
				int k = 0;
				StringBuffer slide = new StringBuffer();
				while (k < settings.DisplayLines && j < lines.length) {
					slide.append(lines[j]);
					slide.append(Main.OS().lineSeparator());
					k++;
					j++;
				}
				newslides.add(slide.toString());
			}
		}
		String[] result = new String[newslides.size()];
		for(int i = 0; i < newslides.size(); i++) {
			result[i] = newslides.get(i);
		}
		return result;
	}

	protected ItemViewer getNewItemViewer() {
		return new TextViewer(window);
	}
}
