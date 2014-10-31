package com.jameslow.limelight.handlers;

import javax.swing.JPanel;

import com.jameslow.FileUtils;
import com.jameslow.limelight.*;

public class TextViewer implements ItemViewer {
	private TextDisplay display;
	
	public TextViewer(LimelightWindow window) {
		display = new TextDisplay(window);
	}
	
	public void blackSlide() {
		display.blackSlide();
	}

	public void blankSlide() {
		display.blankSlide();
	}

	public void displayObject(ItemSlide slide, int index) {
		String title = FileUtils.stripExt(slide.getFilepath());
		display.setSlide(title, (String)slide.getContent(), "", "", "", index);
	}

	public JPanel getItemPanel() {
		return display;
	}
}
