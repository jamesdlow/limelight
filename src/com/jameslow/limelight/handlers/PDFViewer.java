package com.jameslow.limelight.handlers;

import javax.swing.*;
import com.sun.pdfview.*;

public class PDFViewer implements ItemViewer {
	protected PagePanel panel = new PagePanel();
	
	public void blackSlide() {
		//TODO: Implement (maybe this should be done by global)
	}

	public void blankSlide() {
		//TODO: Implement (maybe this should be done by global)
	}

	public void displayObject(ItemSlide slide, int index) {
		PDFPage page = (PDFPage)slide.getContent();
        panel.showPage(page);
	}

	public JPanel getItemPanel() {
        return panel;
	}

}
