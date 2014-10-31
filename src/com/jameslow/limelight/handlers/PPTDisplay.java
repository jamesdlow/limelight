package com.jameslow.limelight.handlers;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import org.apache.poi.hslf.model.*;

import com.jameslow.Main;
import com.jameslow.gui.ImagePane;
import com.jameslow.limelight.*;

public class PPTDisplay extends ImagePane {
	private Slide slide;
	private boolean showslide = true;
	
	public void setSlide(Slide slide) {
		this.slide = slide;
		setBlack(false);
		setShowSlide(true);
	}
	public boolean getShowSlides() {
		return showslide;
	}
	public void setShowSlide(boolean value) {
		showslide = value;
		repaint();
	}
	public void blankSlide() {
		setBlack(false);
		setShowSlide(false);
	}
	public void blackSlide() {
		setBlack(true);
		setShowSlide(false);
	}
	public void setBlack(boolean value) {
		if (value) {
			setBackground(Color.BLACK);
			setShowSlide(false);
		} else {
			LimelightSettings settings = (LimelightSettings) Main.Settings();
			if ("".compareTo(settings.DisplayBackground) != 0) {
				setBackground(new Color(0f,0f,0f,1f));
			} else {
				setBackground(settings.DisplayColor);			
			}	
		}
	} 
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (slide != null && showslide) {
			Graphics2D g2 = (Graphics2D) g;
			Component c = getParent();
			int width;
			int height;
			if (c.getHeight() == 0 || c.getWidth() == 0) {
				Dimension d = getPreferredSize();
				width = (int) d.getWidth();
				height = (int) d.getHeight();
			} else {
				width = c.getWidth();
				height = c.getHeight();
			}
			Dimension page = slide.getSlideShow().getPageSize();
			float widthscale = (float)width/page.width;
			float heightscale = (float)height/page.height;
				float scale;
				if (widthscale < heightscale) {
					scale = widthscale;
				} else {
					scale = heightscale;
				}
			g2.translate((int)(width - page.width*scale)/2, (int)(height - page.height*scale)/2);
			g2.scale(scale, scale);
			slide.draw(g2);
			g2.scale(1,1);
			g2.translate(0,0);
		}
	}
}