package com.jameslow.limelight.handlers;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import com.jameslow.*;
import com.jameslow.limelight.*;

import org.apache.poi.hslf.*;
import org.apache.poi.hslf.model.*;
import org.apache.poi.hslf.usermodel.*;

public class PPTItemHandler extends LimelightItemHandler {
	
	public PPTItemHandler(LimelightWindow window) {
		super(window, LimelightSettings.EXT_POWERPOINT);
	}

	public ItemSlide[] getItemData(File file)  {
        try {
        	FileInputStream is = new FileInputStream(file);
        	SlideShow ppt = new SlideShow(is);
        	is.close();
        	Slide[] pptslides = ppt.getSlides();
        	ItemSlide[] slides = new ItemSlide[pptslides.length];
        	for(int i=0; i<pptslides.length; i++) {
        		slides[i] = new ItemSlide(file.getAbsolutePath(), i, pptslides[i]);
        	}
        	return slides;
        } catch (Exception e) {
        	Main.Logger().warning("Could not load PPT file "+file.getAbsolutePath()+": "+e.getMessage());
        	return null;
        }
	}

	protected ItemViewer getNewItemViewer() {
		return new PPTViewer();
	}

	
	public ComponentListener getComponentListener() {
		return null;
	}

	public ListCellRenderer getListCellRenderer() {
		return new ListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				ItemSlide slide = (ItemSlide)value;
				PPTDisplay panel = new PPTDisplay();
				panel.setBackground(((LimelightSettings) Main.Settings()).DisplayColor);
				int width = window.getSlideViewportWidth();
				panel.setPreferredSize(new Dimension(width,(int) (((float)width)/LimelightSettings.ASPECT_RATIO)));
				panel.setSlide((Slide)slide.getContent());
				return panel;
			}
		};
	}

	public boolean isSinglePage(String filepath) {
		return false;
	}

}
