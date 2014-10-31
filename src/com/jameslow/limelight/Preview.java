package com.jameslow.limelight;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import com.jameslow.*;
import com.jameslow.limelight.LimelightWindow.slidesActionClass;
import com.jameslow.limelight.handlers.*;

public class Preview extends AbstractWindow implements MouseListener {
	private ItemViewer itemviewer;
	private LimelightWindow limelight;
	
	public Preview(LimelightWindow limelight) {
		this(limelight,null);
	}
	public Preview(LimelightWindow limelight, JMenuBar menu) {
		super();
		this.limelight = limelight;
		addKeyListener(limelight.getControls());
		if(menu != null) {
			setJMenuBar(menu);
		}
		setBackground(Color.BLACK);
		setLayout(new BorderLayout());
		addMouseListener(this);
		setVisible(getWindowSettings().getVisible());
	}
	public void postLoad() {}
	public void setItemViewer(ItemViewer viewer) {
		if (itemviewer != null) {
			itemviewer.getItemPanel().setVisible(false);
		} else {
			viewer.getItemPanel().setVisible(false);
		}
		itemviewer = viewer;
		JPanel panel = itemviewer.getItemPanel();
		panel.addKeyListener(limelight.getControls());
		panel.addMouseListener(this);
		add(panel,BorderLayout.CENTER);
		panel.setVisible(true);
	}
	public ItemViewer getItemViewer() {
		return itemviewer;
	}
	public String getDefaultTitle() {
		return "Preview";
	}
	public WindowSettings getDefaultWindowSettings() {
		//return new WindowSettings(640,502,0,265,true,JFrame.NORMAL);
		return new WindowSettings(320,262,0,240+Main.OS().getMenuBar()+LimelightWindow.SPACE,true,JFrame.NORMAL);
	}
	public void setTextIfNullViewer() {
		if (itemviewer == null) {
			setItemViewer(limelight.getTextHandler().getPreviewItemViewer());
		}
	}
	public void blackSlide() {
		setTextIfNullViewer();
		itemviewer.blackSlide();
		limelight.sendAirPlay();
	}
	public void blankSlide() {
		setTextIfNullViewer();
		itemviewer.blankSlide();
		limelight.sendAirPlay();
	}
	public void setSlide(ItemSlide slide, int index) {
		if (itemviewer != limelight.slides.previewViewer) {
			setItemViewer(limelight.slides.previewViewer);
		}
		itemviewer.displayObject(slide,index);
		itemviewer.getItemPanel().repaint();
		limelight.sendAirPlay();
	}
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) {
			limelight.toggleFullScreen();
		}/* else if (e.getClickCount() == 1) {
			if (e.getButton() == e.BUTTON1) {
				limelight.nextSlide();
			} else if (e.getButton() == e.BUTTON3) {
				limelight.prevSlide();
			}
		}*/
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
