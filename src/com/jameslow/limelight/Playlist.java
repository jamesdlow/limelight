package com.jameslow.limelight;

import com.jameslow.*;

public class Playlist {
	private static final String PLAYLIST = "Playlist";
	private static final String PLAYLIST_ITEM = PLAYLIST + "." + "Item";
	private XMLHelper helper;
	
	public Playlist(XMLHelper helper) {
		this.helper = helper;
	}
	public Playlist(String filename) {
		helper = new XMLHelper(filename,Main.OS().appName());
	}
	public Playlist() {
		helper = new XMLHelper(Main.OS().appName());
	}
	public String[] getItems() {
		XMLHelper[] helpers = helper.getSubNodeList(PLAYLIST_ITEM);
		String[] items = new String[helpers.length];
		int i=0;
		for (XMLHelper helper : helpers) {
			items[i++] = helper.getValue();
		}
		return items;
	}
	public void addItems(String[] items) {
		helper.addValues(PLAYLIST_ITEM,items);
	}
	public void replaceItems(String[] items) {
		helper.replaceValues(PLAYLIST_ITEM,items);
	}
	public void save(String filename) {
		helper.save(filename);
	}
	public boolean isPlaylist() {
		XMLHelper temp = helper.getSubNode(PLAYLIST);
		return !temp.getIsNewNode();
	}
}