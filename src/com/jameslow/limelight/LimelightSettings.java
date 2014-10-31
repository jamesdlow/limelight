package com.jameslow.limelight;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.jameslow.*;

public class LimelightSettings extends Settings {
	//Display Constants
	public enum DisplayMode {EVEN, ABSOLUTE, ALL, COMBINE, IGNORE};
	//public static final int DISPLAY_EVEN = 0;			//Try to make slides even
	//public static final int DISPLAY_ABSOLUTE = 1;		//Fill slide until max lines reaches
	//public static final int DISPLAY_ALL = 2;			//Show all lines of a chors / verse
	//public static final int DISPLAY_COMBINE = 3;		//Combine verses / chorus? --not implemented
	//public static final int DISPLAY_IGNORE = 4;			//Ingnore verses / chorus? --not implemented
	public static final String INITIAL_SONG = "Amazing Grace.txt";
	public static final String NATIVE_LIB = "lib/native";
	public static final String REMOTE_LIB = NATIVE_LIB+"/iremoted";
	public static final float ASPECT_RATIO = (float)4/3;
	
	//XML Constants
	public static final String DISPLAY = "Display";
	public static final String SCREEN = DISPLAY + "." + "Screen";
	public static final String STYLEFILE = DISPLAY + "." + "Style";
	public static final String STYLE = "Style";
	public static final String LINES = STYLE + "." + "Lines";
	public static final String METHOD = STYLE + "." + "Method";
	public static final String COLOR = STYLE + "." + "Color";
	public static final String BACKGROUND = STYLE + "." + "Background";
	public static final String FOLDERS = "Folders";
	public static final String FOLDER = FOLDERS + "." + "Folder";
	public static final String FOLDERNAME = "Name";
	public static final String FOLDERPATH = "Path";
	
	//Property Constants
	public static final String EXT = "ext.";
	public static final String EXT_PLAYLIST = EXT+"playlist";
	public static final String EXT_THEME = EXT+"theme";
	public static final String EXT_SONG = EXT+"song";
	public static final String EXT_TEXT = EXT+"text";
	public static final String EXT_AUDIO = EXT+"audio";
	public static final String EXT_VIDEO = EXT+"video";
	public static final String EXT_POWERPOINT = EXT+"powerpoint";
	public static final String EXT_WORD = EXT+"word";
	public static final String EXT_EXCEL = EXT+"excel";
	public static final String EXT_PRESENTATION = EXT+"presentation";
	public static final String EXT_DOCUMENT = EXT+"document";
	public static final String EXT_SPREADSHEET = EXT+"spreadsheet";
	public static final String EXT_FORMATTED = EXT+"formatted";
	public static final String EXT_PDF = EXT+"pdf";
	public static final String EXT_IMAGE = EXT+"image";
	
	//General Settings
	public int DisplayLines;
	public DisplayMode DisplayMethod;
	public String DisplayBackground;
	public String DisplayStyle;
	public Color DisplayColor;
	public int DisplayScreen;
	public TextItem Title, MainText, Copyright, Author, Slide, Message;
	public Map<String,TextItem> TextItems;
	public Map<String,String> Folders;

	public void preSaveSettings() {
		setSetting(SCREEN, DisplayScreen);
		setSetting(STYLE, DisplayStyle);
		setSetting(BACKGROUND, DisplayBackground);
		String key = (String) Folders.keySet().toArray()[0];
		setSetting(FOLDER + "." + FOLDERNAME, key);
		setSetting(FOLDER + "." + FOLDERPATH, Folders.get(key));
	}
	
	public void loadSettings() {
		//General
		DisplayScreen = getSetting(SCREEN,0);
		DisplayStyle = getSetting(STYLE,""); //File name of default visual style

		//Visual Style
		DisplayLines = getSetting(LINES,10);
		int displayint = getSetting(METHOD,DisplayMode.EVEN.ordinal());
		for (DisplayMode method : DisplayMode.values()) {
			if (displayint == method.ordinal()) {
				DisplayMethod = method;
				break;
			}
		}
		if (DisplayMethod == null) {
			DisplayMethod = DisplayMode.EVEN;
		}
		
		DisplayColor = getSetting(COLOR,Color.BLACK);
		DisplayBackground = getSetting(BACKGROUND,"");

		//Folders
		Folders = new HashMap<String,String>();
		XMLHelper[] folders = getXMLHelpers(FOLDER);
		for (int i=0; i<folders.length; i++) {
			String name = folders[i].getValue(FOLDERNAME);
			String value = folders[i].getValue(FOLDERPATH);
			File folder = new File(value);
			if (folder.exists() && folder.isDirectory()) {
				if (!Folders.containsKey(name)) {
					Folders.put(name, value);
				} else {
					Main.Logger().warning("Duplicate name: " + name + " for folder: " + value);
				}
			}
		}
		if (Folders.size() == 0) {	
			try {
				String folder = Main.OS().settingsDir() + Main.OS().fileSeparator() + "Songs";
				(new File(folder)).mkdirs();
				Folders.put("Songs", folder);
				if (FileUtils.IsEmpty(folder)) {
					FileUtils.WriteStream(getResourceAsStream(INITIAL_SONG), folder + Main.OS().fileSeparator() + INITIAL_SONG);
				}
			} catch (Exception e) {
				Main.Logger().severe(e.getMessage());
			}
		}
		loadDefaultSettings();
	}
	public void postSaveSettings() {
		Limelight.refreshFolders();
	}
	public void loadDefaultSettings() {
		TextItems = new HashMap<String,TextItem>();

		MainText = new TextItem("Main",true,false,new Font(null,Font.PLAIN,1),Color.WHITE,0.05f,SwingConstants.CENTER,0.5f,0.15f,SwingConstants.CENTER,SwingConstants.TOP);
		Title = new TextItem("Title",true,true,new Font(null,Font.BOLD,1),Color.WHITE,0.05f,SwingConstants.CENTER,0.5f,0.05f,SwingConstants.CENTER,SwingConstants.TOP);
		Copyright = new TextItem("Copyright",true,true,new Font(null,Font.PLAIN,1),Color.WHITE,0.02f,SwingConstants.LEFT,0.01f,0.95f,SwingConstants.LEFT,SwingConstants.BOTTOM);
		Author = new TextItem("Author",true,true,new Font(null,Font.PLAIN,1),Color.WHITE,0.02f,SwingConstants.LEFT,0.01f,0.9f,SwingConstants.LEFT,SwingConstants.BOTTOM);
		Slide = new TextItem("Slide",false,false,new Font(null,Font.ITALIC,1),Color.WHITE,0.02f,SwingConstants.RIGHT,0.99f,0.95f,SwingConstants.RIGHT,SwingConstants.BOTTOM);
		Message = new TextItem("Message",false,false,new Font(null,Font.BOLD,1),Color.RED,0.05f,SwingConstants.RIGHT,0.99f,0.9f,SwingConstants.RIGHT,SwingConstants.BOTTOM);
		
	}
	public class TextItem {
		public TextItem(String name, boolean show, boolean showfirst, Font font, Color fontcolor,
			float fontsize, int fontalign, float left, float top, int horizontal, int vertical) {
			TextItems.put(name,this);
			Name = name;
			Show = show;
			ShowFirst = showfirst;
			this.font = font;
			FontColor = fontcolor;
			FontSize = fontsize;
			FontAlign = fontalign;
			Left = left;
			Top = top;
			Horizontal = horizontal;
			Vertical = vertical;
		}
		public String Name;
		public boolean Show;
		public boolean ShowFirst;
		public Font font;			//Controls Everything Accept Size
		public Color FontColor;
		public float FontSize;		//% Relative to size of parent height
		public int FontAlign;		//Left/Right/Justify
		public float Left;			//% Relative to size of parent
		public float Top;			//% Relative to size of parent
		public int Horizontal;		//Alignment relative to point speficied by Top/Left
		public int Vertical;		//Alignment relative to point speficied by Top/Left
	}
}
