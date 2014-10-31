package com.jameslow.limelight.handlers;

import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public interface ItemHandler {

	public String[] getExtensions();
	
	public boolean handlesDirectories();
	
	//Get panel to display
	public ItemViewer getPreviewItemViewer();
	
	public ItemViewer getFullscreenItemViewer();
	
	//Whether the given handled filepath is a single slide/page
	//Can be implemented for a specific file (ie, load the file and check)
	//Or all items with a certain extension can be assumed to be multi page
	public boolean isSinglePage(String filepath);
	
	//Get the data for the JList
	public ItemSlide[] getItemData(File file);
	
	//Get so that it can be added to the list
	public ListCellRenderer getListCellRenderer();
	
	//Get so it can be added to the parent window
	public ComponentListener getComponentListener();
}
