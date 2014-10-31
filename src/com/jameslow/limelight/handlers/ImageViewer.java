package com.jameslow.limelight.handlers;

import java.awt.image.*;
import javax.swing.*;

import com.jameslow.gui.ImagePane;
import com.jameslow.limelight.*;

public class ImageViewer implements ItemViewer {
	protected LimelightImagePane imagepane = new LimelightImagePane();
	
	public ImageViewer() {
		imagepane.setDrawMode(ImagePane.DRAW_MODE_ASPECT);
	}
	public void blackSlide() {
		imagepane.setBlack(true);
	}
	public void blankSlide() {
		imagepane.setBlack(true);
	}
	public void displayObject(ItemSlide slide, int index) {
		imagepane.setBlack(false);
		imagepane.setImage((BufferedImage) slide.getContent());
	}
	public JPanel getItemPanel() {
		return imagepane;
	}
}