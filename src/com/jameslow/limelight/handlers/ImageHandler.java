package com.jameslow.limelight.handlers;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;
import com.jameslow.*;
import com.jameslow.gui.ImagePane;
import com.jameslow.limelight.*;

public class ImageHandler extends LimelightItemHandler {
	protected ListCellRenderer renderer;
	protected ImagePane image = new ImagePane();
	
	public ImageHandler(LimelightWindow window) {
		super(window, LimelightSettings.EXT_IMAGE);
		image.setBackground(((LimelightSettings) Main.Settings()).DisplayColor);
		int width = window.getSlideViewportWidth();
		image.setPreferredSize(new Dimension(width,(int) (((float) width)/LimelightSettings.ASPECT_RATIO)));
		image.setDrawMode(ImagePane.DRAW_MODE_ASPECT);
		renderer = new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				return image;
			}
		};
	}

	protected ItemViewer getNewItemViewer() {
		return new ImageViewer();
	}

	public ComponentListener getComponentListener() {
		return null;
	}
	
	public ItemSlide[] getItemData(File file) {
		ItemSlide[] slides = new ItemSlide[1];
		try {
			slides[0] = new ItemSlide(file.getAbsolutePath(),0,ImageIO.read(file));
			image.setImage((BufferedImage) slides[0].getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return slides;
	}

	public ListCellRenderer getListCellRenderer() {
		return renderer;
	}

	public boolean isSinglePage(String filepath) {
		return true;
	}

}
