package com.jameslow.limelight.handlers;

import javax.swing.*;
import org.apache.poi.hslf.model.*;
import com.jameslow.limelight.*;

public class PPTViewer implements ItemViewer {
	protected PPTDisplay display = new PPTDisplay();
	
	public void blackSlide() {
		display.blackSlide();
	}
	public void blankSlide() {
		display.blankSlide();
	}
	public void displayObject(ItemSlide slide, int index) {
		display.setSlide((Slide) slide.getContent());
		display.repaint();
	}
	public JPanel getItemPanel() {
        return display;
	}
}
