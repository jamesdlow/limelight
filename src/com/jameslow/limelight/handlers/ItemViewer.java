package com.jameslow.limelight.handlers;

import javax.swing.JPanel;

public interface ItemViewer {
	//Get panel to display
	public JPanel getItemPanel();
	
	//Display an object on the above viewer
	public void displayObject(ItemSlide slide, int index);
	
	//Blank slide
	public void blankSlide();
	
	//Black slide
	public void blackSlide();
}
